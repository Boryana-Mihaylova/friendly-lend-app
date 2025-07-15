package app.deliveryLocation;
import app.deliveryLocation.model.DeliveryLocation;
import app.deliveryLocation.model.Location;
import app.deliveryLocation.model.Town;
import app.deliveryLocation.repository.DeliveryLocationRepository;
import app.deliveryLocation.servise.DeliveryLocationService;
import app.user.model.User;
import app.web.dto.LocationEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryLocationServiceTest {

    @Mock
    private DeliveryLocationRepository deliveryLocationRepository;

    @InjectMocks
    private DeliveryLocationService deliveryLocationService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
    }

    @Test
    void edit_whenNoExistingLocation_thenCreateNewLocation() {
        LocationEditRequest request = LocationEditRequest.builder()
                .location(Location.CAFE)
                .town(Town.BURGAS)
                .build();

        when(deliveryLocationRepository.findByUserId(userId)).thenReturn(null);
        when(deliveryLocationRepository.save(any(DeliveryLocation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryLocation result = deliveryLocationService.edit(request, user);

        assertThat(result.getLocation()).isEqualTo(Location.CAFE);
        assertThat(result.getTown()).isEqualTo(Town.BURGAS);
        assertThat(result.getUser()).isEqualTo(user);

        verify(deliveryLocationRepository).save(any(DeliveryLocation.class));
    }

    @Test
    void edit_whenExistingLocation_thenUpdateAndSave() {
        DeliveryLocation existing = new DeliveryLocation();
        existing.setUser(user);

        LocationEditRequest request = LocationEditRequest.builder()
                .location(Location.OFFICE)
                .town(Town.PLOVDIV)
                .build();

        when(deliveryLocationRepository.findByUserId(userId)).thenReturn(existing);
        when(deliveryLocationRepository.save(existing)).thenReturn(existing);

        DeliveryLocation result = deliveryLocationService.edit(request, user);

        assertThat(result.getLocation()).isEqualTo(Location.OFFICE);
        assertThat(result.getTown()).isEqualTo(Town.PLOVDIV);

        verify(deliveryLocationRepository).save(existing);
    }

    @Test
    void getByUserId_whenExists_thenReturnLocation() {
        DeliveryLocation location = new DeliveryLocation();
        when(deliveryLocationRepository.findByUserId(userId)).thenReturn(location);

        DeliveryLocation result = deliveryLocationService.getByUserId(userId);

        assertThat(result).isEqualTo(location);
        verify(deliveryLocationRepository).findByUserId(userId);
    }
}
