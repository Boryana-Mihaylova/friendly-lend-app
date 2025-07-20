package app.purchase;


import app.exception.DomainException;
import app.item.model.Gender;
import app.item.model.Item;
import app.item.model.Period;
import app.item.model.SizeItem;
import app.item.service.ItemService;
import app.purchase.model.ItemPurchase;
import app.purchase.repository.PurchaseRepository;
import app.purchase.service.PurchaseService;
import app.user.model.User;
import app.web.dto.ItemPurchaseRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void givenValidItemPurchaseRequest_whenCreatePurchase_thenPurchaseIsSaved() {

        UUID itemId = UUID.randomUUID();
        User user = new User();
        Item item = new Item();
        item.setId(itemId);

        ItemPurchaseRequest request = ItemPurchaseRequest.builder()
                .name("Test Item")
                .description("Test Description")
                .imageUrl("http://test.com/image.jpg")
                .gender(Gender.UNISEX)
                .size(SizeItem.M)
                .price(BigDecimal.valueOf(100))
                .period(Period.ONE_MONTH)
                .itemId(itemId)
                .build();

        ItemPurchase itemPurchase = ItemPurchase.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .gender(request.getGender())
                .size(request.getSize())
                .price(request.getPrice())
                .period(request.getPeriod())
                .owner(user)
                .item(item)
                .build();

        when(itemService.getItemById(itemId)).thenReturn(item);
        when(purchaseRepository.save(any(ItemPurchase.class))).thenReturn(itemPurchase);


        ItemPurchase savedPurchase = purchaseService.createPurchase(request, user);


        assertThat(savedPurchase).isNotNull();
        assertThat(savedPurchase.getName()).isEqualTo("Test Item");
        verify(purchaseRepository).save(any(ItemPurchase.class));
    }

    @Test
    void givenInvalidItemId_whenCreatePurchase_thenThrowsException() {

        UUID invalidItemId = UUID.randomUUID();
        User user = new User();
        ItemPurchaseRequest request = ItemPurchaseRequest.builder()
                .itemId(invalidItemId)
                .build();

        when(itemService.getItemById(invalidItemId)).thenThrow(new DomainException("Item not found"));


        assertThrows(DomainException.class, () -> purchaseService.createPurchase(request, user));
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void givenValidPurchaseId_whenGetById_thenReturnPurchase() {

        UUID purchaseId = UUID.randomUUID();
        ItemPurchase itemPurchase = new ItemPurchase();
        itemPurchase.setId(purchaseId);

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(itemPurchase));


        ItemPurchase foundPurchase = purchaseService.getById(purchaseId);


        assertThat(foundPurchase).isNotNull();
        assertThat(foundPurchase.getId()).isEqualTo(purchaseId);
        verify(purchaseRepository).findById(purchaseId);
    }

    @Test
    void givenInvalidPurchaseId_whenGetById_thenThrowsException() {

        UUID invalidPurchaseId = UUID.randomUUID();

        when(purchaseRepository.findById(invalidPurchaseId)).thenReturn(Optional.empty());


        assertThrows(DomainException.class, () -> purchaseService.getById(invalidPurchaseId));
        verify(purchaseRepository).findById(invalidPurchaseId);
    }

    @Test
    void givenExistingPurchases_whenGetAllByOwnerId_thenReturnList() {

        UUID ownerId = UUID.randomUUID();
        List<ItemPurchase> purchases = List.of(new ItemPurchase(), new ItemPurchase());

        when(purchaseRepository.findByOwnerId(ownerId)).thenReturn(purchases);


        List<ItemPurchase> result = purchaseService.getAllByOwnerId(ownerId);


        assertThat(result).hasSize(2);
        verify(purchaseRepository).findByOwnerId(ownerId);
    }
}
