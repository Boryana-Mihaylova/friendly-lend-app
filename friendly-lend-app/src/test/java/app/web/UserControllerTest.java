package app.web;
import app.notification.model.Notification;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.UserEditRequest;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = app.web.mapper.UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final UUID mockUserId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        var auth = new app.security.AuthenticationMetadata(
                mockUserId,
                "testuser",
                "test@example.com",
                UserRole.ADMIN,
                true
        );
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(auth, null, "ROLE_ADMIN")
        );
    }

    @Test
    @WithMockUser
    void testGetProfileMenu_ReturnsForm() throws Exception {
        User mockUser = new User();
        mockUser.setId(mockUserId);
        Mockito.when(userService.getById(mockUserId)).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + mockUserId + "/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-menu"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userEditRequest"));
    }

    @Test
    @WithMockUser
    void testUpdateUserProfile_WithValidData_ShouldRedirect() throws Exception {
        UserEditRequest request = UserEditRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + mockUserId + "/profile")
                        .with(csrf())
                        .flashAttr("userEditRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser
    void testUpdateUserProfile_WithValidationErrors_ShouldReturnForm() throws Exception {
        User mockUser = new User();
        mockUser.setId(mockUserId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setRole(UserRole.USER);
        mockUser.setActive(true);

        Mockito.when(userService.getById(mockUserId)).thenReturn(mockUser);

        UserEditRequest invalidRequest = UserEditRequest.builder()
                .firstName("thisnameiswaytoolongforvalidation")
                .lastName("anothernameoverlimit")
                .email("invalid-email")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + mockUserId + "/profile")
                        .with(csrf())
                        .flashAttr("userEditRequest", invalidRequest))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-menu"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userEditRequest"));
    }

    @Test
    @WithMockUser
    void testGetMyProfile_ShouldReturnProfileWithNotifications() throws Exception {
        User mockUser = new User();
        mockUser.setId(mockUserId);

        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setRole(UserRole.USER);
        mockUser.setActive(true);

        Mockito.when(userService.getById(mockUserId)).thenReturn(mockUser);
        Mockito.when(userService.getUnreadNotifications(mockUserId)).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + mockUserId + "/my-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("userProfilePage"))
                .andExpect(model().attributeExists("notifications"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers_ShouldReturnView() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(new User()));

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSwitchUserRole_ShouldRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + mockUserId + "/role").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSwitchUserStatus_ShouldRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + mockUserId + "/status").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }
}
