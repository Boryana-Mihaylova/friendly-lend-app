package app.web;

import app.favorite.service.FavoriteService;
import app.item.model.Item;
import app.item.service.ItemService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.CreateFavorite;
import app.web.mapper.FavoriteController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(FavoriteController.class)
public class FavoriteControllerApiTest {


    @MockBean
    private UserService userService;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFavorites_whenAuthenticated_thenReturnFavoritesView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);
        User user = new User();
        user.setId(userId);

        when(userService.getById(userId)).thenReturn(user);
        when(favoriteService.getFavoritesByUser(user)).thenReturn(List.of());

        mockMvc.perform(get("/favorites").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("favorites"))
                .andExpect(model().attributeExists("favorites", "user"));

        verify(favoriteService).getFavoritesByUser(user);
    }

    @Test
    void addToFavorite_whenValidId_thenReturnMyFavoriteView() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        CreateFavorite createFavorite = CreateFavorite.builder().name("Test").imageUrl("img").itemId(itemId).build();

        when(userService.getById(userId)).thenReturn(user);
        when(itemService.getItemById(itemId)).thenReturn(item);
        when(itemService.convertToItemFavorite(item)).thenReturn(createFavorite);

        mockMvc.perform(get("/favorites/add-to-favorite/" + itemId).with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("my-favorite"))
                .andExpect(model().attributeExists("createFavorite", "user"));

        verify(favoriteService).createFavoriteItem(createFavorite, user);
    }

    @Test
    void removeFavorite_whenValidId_thenRedirectToFavorites() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID favoriteId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);
        User user = new User();
        user.setId(userId);

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/favorites/remove/" + favoriteId).with(user(principal)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/favorites"));

        verify(favoriteService).removeFavoriteById(favoriteId, user);
    }
}
