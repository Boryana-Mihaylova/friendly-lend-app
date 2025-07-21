package app.deliveryLocation.servise;

import app.deliveryLocation.model.DeliveryLocation;
import app.deliveryLocation.repository.DeliveryLocationRepository;
import app.user.model.User;
import app.web.dto.LocationEditRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeliveryLocationService {

    private final DeliveryLocationRepository pickupLocationRepository;


    @Autowired
    public DeliveryLocationService(DeliveryLocationRepository pickupLocationRepository) {
        this.pickupLocationRepository = pickupLocationRepository;

    }

    public DeliveryLocation edit(LocationEditRequest locationEditRequest, User user) {

        DeliveryLocation existing = pickupLocationRepository.findByUserId(user.getId());

        if (existing != null) {

            existing.setLocation(locationEditRequest.getLocation());
            existing.setTown(locationEditRequest.getTown());
            return pickupLocationRepository.save(existing);
        }


        DeliveryLocation pickupLocation = DeliveryLocation.builder()

                .location(locationEditRequest.getLocation())
                .town(locationEditRequest.getTown())
                .user(user)
                .build();

        pickupLocation = pickupLocationRepository.save(pickupLocation);

        return pickupLocation;
    }

    public DeliveryLocation getByUserId(UUID userId) {
        return pickupLocationRepository.findByUserId(userId);
    }
}
