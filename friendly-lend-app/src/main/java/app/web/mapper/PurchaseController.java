package app.web.mapper;

import app.item.model.Item;
import app.item.service.ItemService;
import app.purchase.service.PurchaseService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.ItemPurchaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private final UserService userService;
    private final ItemService itemService;
    private final PurchaseService purchaseService;


    @Autowired
    public PurchaseController(UserService userService, ItemService itemService, PurchaseService purchaseService) {
        this.userService = userService;
        this.itemService = itemService;
        this.purchaseService = purchaseService;
    }



    @GetMapping("/rent-to-bag/{id}")
    public ModelAndView getNewPurchase(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());


        Item item = itemService.getItemById(id);

        ItemPurchaseRequest itemPurchaseRequest = itemService.convertToItemPurchase(item);

        purchaseService.createPurchase(itemPurchaseRequest, user);


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("my-bag");
        modelAndView.addObject("ItemPurchaseRequest", itemPurchaseRequest);
        modelAndView.addObject("user", user);

        return modelAndView;

    }
}
