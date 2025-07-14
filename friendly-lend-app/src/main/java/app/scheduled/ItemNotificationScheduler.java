package app.scheduled;

import app.item.model.Item;
import app.item.model.ItemStatus;
import app.item.repository.ItemRepository;
import app.notification.model.Notification;
import app.notification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class ItemNotificationScheduler {

    private final ItemRepository itemRepository;
    private final NotificationRepository notificationRepository;

    public ItemNotificationScheduler(ItemRepository itemRepository, NotificationRepository notificationRepository) {
        this.itemRepository = itemRepository;
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void archiveOldAvailableItems() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(60);
        Date thresholdDate = Timestamp.valueOf(threshold);

        List<Item> oldItems = itemRepository.findAllByStatusAndCreatedAtBefore(ItemStatus.AVAILABLE, thresholdDate);

        for (Item item : oldItems) {
            Notification notification = Notification.builder()
                    .user(item.getOwner())
                    .message("Здравей! Артикулът ти „" + item.getName() + "“ не е бил наеман от 60 дни. Ако интересът остане нисък, той ще бъде архивиран автоматично след 7 дни.")
                    .createdAt(new Date())
                    .read(false)
                    .build();

            notificationRepository.save(notification);
        }

        System.out.println("✔ Изпратени известия за " + oldItems.size() + " артикула без активност.");
    }
}
