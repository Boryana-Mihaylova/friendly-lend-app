package app.item.service;


import app.exception.DomainException;
import app.item.model.Item;
import app.item.repository.ItemRepository;
import app.user.model.User;

import app.web.dto.CreateNewItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public ItemService(ItemRepository itemRepository, PasswordEncoder passwordEncoder) {

        this.itemRepository = itemRepository;
        this.passwordEncoder = passwordEncoder;

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



}
