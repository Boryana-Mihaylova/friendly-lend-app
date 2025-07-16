package app.web;
import app.item.model.Item;
import app.item.model.ItemStatus;
import app.item.repository.ItemRepository;
import app.notification.model.Notification;
import app.notification.repository.NotificationRepository;
import app.scheduled.ItemNotificationScheduler;
import app.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
public class ItemNotificationSchedulerTest {

    private ItemRepository itemRepository;
    private NotificationRepository notificationRepository;
    private ItemNotificationScheduler scheduler;

    @BeforeEach
    void setup() {
        itemRepository = mock(ItemRepository.class);
        notificationRepository = mock(NotificationRepository.class);
        scheduler = new ItemNotificationScheduler(itemRepository, notificationRepository);
    }

    @Test
    void archiveOldAvailableItems_shouldCreateNotificationForOldItems() {

        User user = User.builder()
                .id(java.util.UUID.randomUUID())
                .username("user")
                .email("test@example.com")
                .isActive(true)
                .build();

        Item item = Item.builder()
                .id(java.util.UUID.randomUUID())
                .name("Test Item")
                .status(ItemStatus.AVAILABLE)
                .createdAt(Timestamp.valueOf(LocalDateTime.now().minusDays(70)))
                .owner(user)
                .build();

        when(itemRepository.findAllByStatusAndCreatedAtBefore(
                eq(ItemStatus.AVAILABLE),
                any(Date.class))
        ).thenReturn(List.of(item));

        scheduler.archiveOldAvailableItems();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        Notification savedNotification = captor.getValue();
        assertThat(savedNotification.getUser()).isEqualTo(user);
        assertThat(savedNotification.getMessage()).contains("не е бил наеман от 60 дни");
        assertThat(savedNotification.isRead()).isFalse();
    }
}
