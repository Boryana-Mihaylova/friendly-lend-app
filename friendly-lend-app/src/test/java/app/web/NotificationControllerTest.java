package app.web;
import app.notification.model.Notification;
import app.user.model.User;
import app.user.service.UserService;
import app.web.mapper.NotificationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    @WithMockUser
    void testGetUnreadNotifications_ShouldReturnListOfNotifications() throws Exception {
        Notification mockNotification = Notification.builder()
                .id(UUID.randomUUID())
                .message("Your item has been archived.")
                .createdAt(new Date())
                .read(false)
                .user(new User())
                .build();

        Mockito.when(userService.getUnreadNotifications(userId))
                .thenReturn(List.of(mockNotification));

        mockMvc.perform(get("/api/notifications/unread/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Your item has been archived."))
                .andExpect(jsonPath("$[0].read").value(false));
    }
}
