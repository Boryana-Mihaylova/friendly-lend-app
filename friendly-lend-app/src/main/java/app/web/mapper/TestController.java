package app.web.mapper;

import app.scheduled.ItemNotificationScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    /**
     * Demo-only endpoint for manually triggering the item scheduler.
     * Not intended for production use.
     */

    private final ItemNotificationScheduler itemNotificationScheduler;

    @Autowired
    public TestController(ItemNotificationScheduler itemNotificationScheduler) {
        this.itemNotificationScheduler = itemNotificationScheduler;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test/inactive-notifications")
    public String triggerManually() {
        itemNotificationScheduler.archiveOldAvailableItems();
        return "redirect:/home";
    }
}
