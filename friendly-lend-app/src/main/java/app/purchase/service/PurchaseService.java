package app.purchase.service;


import app.exception.DomainException;
import app.item.model.Item;
import app.item.service.ItemService;
import app.purchase.model.ItemPurchase;
import app.purchase.repository.PurchaseRepository;
import app.user.model.User;
import app.web.dto.ItemPurchaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final ItemService itemService;

    private final PasswordEncoder passwordEncoder;



    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, ItemService itemService, PasswordEncoder passwordEncoder) {
        this.purchaseRepository = purchaseRepository;
        this.itemService = itemService;


        this.passwordEncoder = passwordEncoder;
    }


    public ItemPurchase getById(UUID id) {
        return purchaseRepository.findById(id).orElseThrow(() -> new DomainException("Transaction with id [%s] does not exist.".formatted(id)));
    }



    public ItemPurchase createPurchase(ItemPurchaseRequest itemPurchaseRequest, User user) {

        // Извличаме артикула с ID-то от заявката
        Item item = itemService.getItemById(itemPurchaseRequest.getItemId());

        if (item == null) {
            throw new DomainException("Item with id [%s] does not exist.".formatted(itemPurchaseRequest.getItemId()));
        }

        // Създаване на нова поръчка
        ItemPurchase itemPurchase = ItemPurchase.builder()
                .name(itemPurchaseRequest.getName())
                .description(itemPurchaseRequest.getDescription())
                .imageUrl(itemPurchaseRequest.getImageUrl())
                .gender(itemPurchaseRequest.getGender())
                .size(itemPurchaseRequest.getSize())
                .price(itemPurchaseRequest.getPrice())
                .period(itemPurchaseRequest.getPeriod())
                .owner(user)
                .item(item)  // Добавяме артикула към поръчката
                .build();

        // Записваме поръчката в базата
        return purchaseRepository.save(itemPurchase);
    }


    public List<ItemPurchase> getAllByOwnerId(UUID ownerId) {

        return purchaseRepository.findByOwnerId(ownerId);
    }
}
