package app.web;
import app.item.model.Item;

import app.item.model.Category;
import app.item.model.Gender;
import app.item.model.Period;
import app.item.model.SizeItem;
import app.item.service.ItemService;
import app.purchase.service.PurchaseService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateNewItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = app.web.mapper.ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private PurchaseService purchaseService;

    private final UUID mockUserId = UUID.randomUUID();
    private AuthenticationMetadata authenticationMetadata;

    @BeforeEach
    void setup() {
        authenticationMetadata = new AuthenticationMetadata(
                mockUserId,
                "testuser",
                "test@example.com",
                UserRole.USER,
                false
        );

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(authenticationMetadata, null, "ROLE_USER")
        );
    }

    @Test
    @WithMockUser
    void testGetNewItemPage() throws Exception {
        Mockito.when(userService.getById(Mockito.any())).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-item"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("createNewItem"));
    }

    @Test
    @WithMockUser
    void testCreateNewItem_WithValidationErrors() throws Exception {
        Mockito.when(userService.getById(Mockito.any())).thenReturn(new User());

        CreateNewItem invalidDto = CreateNewItem.builder().build();

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .with(csrf())
                        .flashAttr("createNewItem", invalidDto))
                .andExpect(status().isOk())
                .andExpect(view().name("add-item"));
    }

    @Test
    @WithMockUser
    void testCreateNewItem_WithValidData_ShouldRedirect() throws Exception {
        User user = new User();
        Item createdItem = Item.builder().id(UUID.randomUUID()).build();

        Mockito.when(userService.getById(Mockito.any())).thenReturn(user);
        Mockito.when(itemService.create(Mockito.any(), Mockito.any())).thenReturn(createdItem);

        CreateNewItem validDto = CreateNewItem.builder()
                .name("Jacket")
                .description("Warm winter jacket")
                .imageUrl("http://example.com/image.jpg")
                .category(Category.CLOTHES)
                .gender(Gender.UNISEX)
                .size(SizeItem.L)
                .period(Period.TWO_WEEKS)
                .price(BigDecimal.valueOf(1.00))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .with(csrf())
                        .flashAttr("createNewItem", validDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/my-closet"));
    }

    @Test
    @WithMockUser
    void testGetMyCloset_ShouldShowUserItems() throws Exception {
        User user = new User();
        List<Item> items = List.of(new Item());

        Mockito.when(userService.getById(Mockito.any())).thenReturn(user);
        Mockito.when(itemService.getAvailableItemsByOwner(Mockito.any())).thenReturn(items);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/my-closet"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-closet"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @WithMockUser
    void testGetItemsForOthers_ShouldReturnOffers() throws Exception {
        User user = new User();
        Mockito.when(userService.getById(Mockito.any())).thenReturn(user);

        User mockOwner = new User();
        mockOwner.setUsername("testuser");

        Item item = new Item();
        item.setOwner(mockOwner);

        List<Item> others = List.of(item);
        Mockito.when(itemService.getItemsFromOthers(Mockito.any())).thenReturn(others);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/offers"))
                .andExpect(status().isOk())
                .andExpect(view().name("offers"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("itemsFromOthers"));
    }

    @Test
    @WithMockUser
    void testRentItem_ShouldRedirect() throws Exception {
        User borrower = new User();
        UUID itemId = UUID.randomUUID();

        Mockito.when(userService.getById(Mockito.any())).thenReturn(borrower);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/rent/" + itemId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/offers"));
    }
}
