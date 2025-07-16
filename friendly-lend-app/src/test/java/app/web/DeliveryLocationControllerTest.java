package app.web;

import app.deliveryLocation.model.DeliveryLocation;
import app.deliveryLocation.model.Location;
import app.deliveryLocation.model.Town;
import app.deliveryLocation.servise.DeliveryLocationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.LocationEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@WebMvcTest(controllers = app.web.mapper.DeliveryLocationController.class)
public class DeliveryLocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private DeliveryLocationService pickupLocationService;

    private final UUID mockUserId = UUID.randomUUID();
    private AuthenticationMetadata authenticationMetadata;

    @BeforeEach
    void setupSecurityContext() {
        authenticationMetadata = new AuthenticationMetadata(
                mockUserId,
                "testuser",
                "test@example.com",
                UserRole.USER,
                false
        );

        // Поставяме го като principal в security context
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(authenticationMetadata, null, "ROLE_USER")
        );
    }

    @Test
    @WithMockUser
    void testGetNewPickupLocationPage_ReturnsFormWithUserAndDto() throws Exception {
        User mockUser = new User();
        Mockito.when(userService.getById(Mockito.any())).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/locations/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-location"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("locationEditRequest"));
    }

    @Test
    @WithMockUser
    void testEditPickupLocation_WithValidationErrors_ShouldReturnSameForm() throws Exception {
        User mockUser = new User();
        Mockito.when(userService.getById(Mockito.any())).thenReturn(mockUser);

        LocationEditRequest requestDto = LocationEditRequest.builder()
                .location(null)
                .town(null)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/locations")
                        .with(csrf())
                        .flashAttr("locationEditRequest", requestDto))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-location"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    void testEditPickupLocation_WithValidInput_ShouldRedirect() throws Exception {
        User mockUser = new User();
        Mockito.when(userService.getById(Mockito.any())).thenReturn(mockUser);

        LocationEditRequest requestDto = LocationEditRequest.builder()
                .location(Location.UNIVERSITY)
                .town(Town.SOFIA)
                .build();

        Mockito.when(pickupLocationService.edit(Mockito.any(), Mockito.any()))
                .thenReturn(new DeliveryLocation());

        mockMvc.perform(MockMvcRequestBuilders.post("/locations")
                        .with(csrf())
                        .flashAttr("locationEditRequest", requestDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locations/my-location"));
    }

    @Test
    @WithMockUser
    void testGetMyLocation_ReturnsMyLocationPageWithModel() throws Exception {
        User mockUser = new User();
        DeliveryLocation mockLocation = new DeliveryLocation();

        Mockito.when(userService.getById(Mockito.any())).thenReturn(mockUser);
        Mockito.when(pickupLocationService.getByUserId(Mockito.any())).thenReturn(mockLocation);

        mockMvc.perform(MockMvcRequestBuilders.get("/locations/my-location"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-location"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("location"));
    }
}
