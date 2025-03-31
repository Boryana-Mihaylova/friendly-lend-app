package app.purchase.repository;


import app.purchase.model.ItemPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<ItemPurchase, UUID> {

    List<ItemPurchase> findByOwnerId(UUID ownerId);

}
