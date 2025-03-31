package app.purchase.service;


import app.exception.DomainException;
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

    private final PasswordEncoder passwordEncoder;



    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository, PasswordEncoder passwordEncoder) {
        this.purchaseRepository = purchaseRepository;


        this.passwordEncoder = passwordEncoder;
    }


    public ItemPurchase getById(UUID id) {
        return purchaseRepository.findById(id).orElseThrow(() -> new DomainException("Transaction with id [%s] does not exist.".formatted(id)));
    }



    public ItemPurchase createPurchase(ItemPurchaseRequest itemPurchaseRequest, User user) {

        ItemPurchase itemPurchase = ItemPurchase.builder()
                .name(itemPurchaseRequest.getName())
                .description(itemPurchaseRequest.getDescription())
                .imageUrl(itemPurchaseRequest.getImageUrl())
                .gender(itemPurchaseRequest.getGender())
                .size(itemPurchaseRequest.getSize())
                .price(itemPurchaseRequest.getPrice())
                .period(itemPurchaseRequest.getPeriod())
                .owner(user)
                .build();


        return purchaseRepository.save(itemPurchase);
    }


    public List<ItemPurchase> getAllByOwnerId(UUID ownerId) {

        return purchaseRepository.findByOwnerId(ownerId);
    }
}
