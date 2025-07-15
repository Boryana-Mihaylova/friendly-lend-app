package app.item;


import app.exception.DomainException;
import app.item.model.*;
import app.item.repository.ItemRepository;
import app.item.service.ItemService;
import app.purchase.model.ItemPurchase;
import app.purchase.repository.PurchaseRepository;
import app.user.model.User;
import app.web.dto.CreateFavorite;
import app.web.dto.CreateNewItem;
import app.web.dto.ItemPurchaseRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void givenExistingItems_whenGetAll_thenReturnList() {
        List<Item> items = List.of(new Item(), new Item());
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.getAll();

        assertThat(result).hasSize(2);
        verify(itemRepository).findAll();
    }

    @Test
    void givenExistingItem_whenGetById_thenReturnItem() {
        UUID itemId = UUID.randomUUID();
        Item item = new Item();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.getById(itemId);

        assertThat(result).isEqualTo(item);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void givenNonExistingItem_whenGetById_thenThrowException() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> itemService.getById(itemId));
        verify(itemRepository).findById(itemId);
    }

    @Test
    void givenOwnerId_whenGetAllByOwnerId_thenReturnItems() {
        UUID ownerId = UUID.randomUUID();
        List<Item> items = List.of(new Item(), new Item());
        when(itemRepository.findByOwnerId(ownerId)).thenReturn(items);

        List<Item> result = itemService.getAllByOwnerId(ownerId);

        assertThat(result).hasSize(2);
        verify(itemRepository).findByOwnerId(ownerId);
    }

    @Test
    void givenItem_whenConvertToItemPurchase_thenReturnDTO() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");

        ItemPurchaseRequest dto = itemService.convertToItemPurchase(item);

        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Description");
    }

    @Test
    void givenItem_whenConvertToItemFavorite_thenReturnDTO() {
        Item item = new Item();
        item.setName("Test Item");

        CreateFavorite dto = itemService.convertToItemFavorite(item);

        assertThat(dto.getName()).isEqualTo("Test Item");
    }

    @Test
    void givenNewItemRequest_whenCreate_thenReturnSavedItem() {
        CreateNewItem createNewItem = CreateNewItem.builder()
                .name("Hat")
                .description("Cool hat")
                .category(Category.JEWELLERY)
                .gender(Gender.UNISEX)
                .size(SizeItem.M)
                .period(Period.ONE_MONTH)
                .imageUrl("http://img.jpg")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("bob")
                .build();

        Item savedItem = new Item();
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.create(createNewItem, user);

        assertThat(result).isEqualTo(savedItem);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void givenItemAndBorrower_whenMarkAsBorrowed_thenBorrowerIsSet() {
        UUID itemId = UUID.randomUUID();
        Item item = new Item();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        User borrower = new User();
        borrower.setId(UUID.randomUUID());

        itemService.markAsBorrowed(itemId, borrower);

        assertThat(item.getBorrower()).isEqualTo(borrower);
        verify(itemRepository).save(item);
    }

    @Test
    void givenItemsAndPurchases_whenGetItemsFromOthers_thenFilteredListReturned() {
        UUID currentUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        User currentUser = User.builder().id(currentUserId).build();
        User otherUser = User.builder().id(otherUserId).build();

        Item item1 = Item.builder().id(UUID.randomUUID()).owner(currentUser).build(); // should be filtered out
        Item item2 = Item.builder().id(UUID.randomUUID()).owner(otherUser).build();   // should stay
        Item item3 = Item.builder().id(UUID.randomUUID()).owner(otherUser).build();   // but is rented

        ItemPurchase purchase = new ItemPurchase();
        purchase.setItem(item3);

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2, item3));
        when(purchaseRepository.findAll()).thenReturn(List.of(purchase));

        List<Item> result = itemService.getItemsFromOthers(currentUserId);

        assertThat(result).containsExactly(item2);
    }

    @Test
    void givenOwnerId_whenGetAvailableItemsByOwner_thenReturnFilteredList() {
        UUID ownerId = UUID.randomUUID();
        List<Item> availableItems = List.of(new Item(), new Item());

        when(itemRepository.findAllByOwnerIdAndBorrowerIsNull(ownerId)).thenReturn(availableItems);

        List<Item> result = itemService.getAvailableItemsByOwner(ownerId);

        assertThat(result).hasSize(2);
        verify(itemRepository).findAllByOwnerIdAndBorrowerIsNull(ownerId);
    }
}
