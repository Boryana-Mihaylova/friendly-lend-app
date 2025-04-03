package app.deliveryLocation.repository;

import app.deliveryLocation.model.DeliveryLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryLocationRepository extends JpaRepository<DeliveryLocation, UUID> {

    List<DeliveryLocation> findByOwnerId(UUID ownerId);
}
