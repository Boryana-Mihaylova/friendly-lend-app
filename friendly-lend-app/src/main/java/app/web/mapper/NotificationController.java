package app.web.mapper;

import app.user.service.UserService;
import app.web.dto.NotificationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final UserService userService;

    public NotificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/unread/{userId}")
    public List<NotificationResponse> getUnreadNotifications(@PathVariable UUID userId) {
        return userService.getUnreadNotifications(userId).stream()
                .map(notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .message(notification.getMessage())
                        .createdAt(notification.getCreatedAt())
                        .read(notification.isRead())
                        .build())
                .collect(Collectors.toList());
    }
}
