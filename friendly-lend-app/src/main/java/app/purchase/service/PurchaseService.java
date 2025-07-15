package app.purchase.service;


import app.exception.DomainException;
import app.item.model.Item;
import app.item.service.ItemService;
import app.purchase.model.ItemPurchase;
import app.purchase.repository.PurchaseRepository;
import app.user.model.User;
import app.web.dto.ItemPurchaseRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final ItemService itemService;



    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, ItemService itemService) {
        this.purchaseRepository = purchaseRepository;
        this.itemService = itemService;

    }


    public ItemPurchase getById(UUID id) {
        return purchaseRepository.findById(id).orElseThrow(() -> new DomainException("Transaction with id [%s] does not exist.".formatted(id)));
    }



    public ItemPurchase createPurchase(ItemPurchaseRequest itemPurchaseRequest, User user) {


        Item item = itemService.getItemById(itemPurchaseRequest.getItemId());



        itemService.markAsBorrowed(item.getId(), user);


        ItemPurchase itemPurchase = ItemPurchase.builder()
                .name(itemPurchaseRequest.getName())
                .description(itemPurchaseRequest.getDescription())
                .imageUrl(itemPurchaseRequest.getImageUrl())
                .gender(itemPurchaseRequest.getGender())
                .size(itemPurchaseRequest.getSize())
                .price(itemPurchaseRequest.getPrice())
                .period(itemPurchaseRequest.getPeriod())
                .owner(user)
                .item(item)
                .build();


        return purchaseRepository.save(itemPurchase);
    }


    public List<ItemPurchase> getAllByOwnerId(UUID ownerId) {

        return purchaseRepository.findByOwnerId(ownerId);
    }
}
