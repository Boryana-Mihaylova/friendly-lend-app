package app.item.service;


import app.exception.DomainException;


import app.item.model.Item;
import app.item.repository.ItemRepository;
import app.purchase.repository.PurchaseRepository;
import app.user.model.User;

import app.web.dto.CreateFavorite;
import app.web.dto.CreateNewItem;
import app.web.dto.ItemPurchaseRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final PurchaseRepository purchaseRepository;


    @Autowired
    public ItemService(ItemRepository itemRepository, PurchaseRepository purchaseRepository) {
        this.itemRepository = itemRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public Item create(CreateNewItem createNewItem, User user) {

        Item item = Item.builder()
                .imageUrl(createNewItem.getImageUrl())
                .name(createNewItem.getName())
                .description(createNewItem.getDescription())
                .category(createNewItem.getCategory())
                .gender(createNewItem.getGender())
                .size(createNewItem.getSize())
                .price(BigDecimal.valueOf(1.00))
                .period(createNewItem.getPeriod())
                .owner(user)
                .build();


        item = itemRepository.save(item);

        return item;
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }


    public Item getById(UUID id) {

        return itemRepository.findById(id)
                .orElseThrow(() -> new DomainException("Item with id [%s] does not exist.".formatted(id)));
    }


    public List<Item> getAllByOwnerId(UUID ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }


    public List<Item> getItemsFromOthers(UUID userId) {

        List<Item> allItems = itemRepository.findAll();


        List<UUID> rentedItemIds = purchaseRepository.findAll()
                .stream()
                .map(itemPurchase -> itemPurchase.getItem().getId())
                .collect(Collectors.toList());


        return allItems.stream()
                .filter(item -> !item.getOwner().getId().equals(userId))
                .filter(item -> !rentedItemIds.contains(item.getId()))
                .collect(Collectors.toList());
    }


    public Item getItemById(UUID id) {
        Optional<Item> itemOptional = itemRepository.findById(id);

        return itemOptional.orElseThrow(() -> new DomainException("Item with id [%s] does not exist.".formatted(id)));
    }

    public ItemPurchaseRequest convertToItemPurchase(Item item) {
        return ItemPurchaseRequest.builder()
                .name(item.getName())
                .description(item.getDescription())
                .imageUrl(item.getImageUrl())
                .gender(item.getGender())
                .period(item.getPeriod())
                .size(item.getSize())
                .price(item.getPrice())
                .itemId(item.getId())
                .build();
    }

    public CreateFavorite convertToItemFavorite(Item item) {
        return CreateFavorite.builder()
                .name(item.getName())
                .imageUrl(item.getImageUrl())
                .itemId(item.getId())
                .build();
    }
}
