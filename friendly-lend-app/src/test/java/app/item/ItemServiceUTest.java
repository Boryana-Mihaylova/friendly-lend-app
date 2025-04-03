package app.item;


import app.exception.DomainException;
import app.item.model.Item;
import app.item.repository.ItemRepository;
import app.item.service.ItemService;
import app.purchase.repository.PurchaseRepository;
import app.web.dto.CreateFavorite;
import app.web.dto.ItemPurchaseRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private PasswordEncoder passwordEncoder;


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


}
