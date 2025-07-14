package app.web.mapper;

import app.scheduled.ItemNotificationScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    private final ItemNotificationScheduler itemNotificationScheduler;

    @Autowired
    public TestController(ItemNotificationScheduler itemNotificationScheduler) {
        this.itemNotificationScheduler = itemNotificationScheduler;
    }

    @GetMapping("/test/inactive-notifications")
    public String triggerManually() {
        itemNotificationScheduler.archiveOldAvailableItems();
        return "redirect:/home";
    }
}
