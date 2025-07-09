package app.web.mapper;


import app.item.model.Item;
import app.item.service.ItemService;
import app.purchase.service.PurchaseService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateNewItem;


import app.web.dto.ItemPurchaseRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/items")
public class ItemController {


    private final UserService userService;
    private final ItemService itemService;

    private final PurchaseService purchaseService;

    @Autowired
    public ItemController(UserService userService, ItemService itemService, PurchaseService purchaseService) {
        this.userService = userService;
        this.itemService = itemService;
        this.purchaseService = purchaseService;
    }



    @GetMapping("/new")
    public ModelAndView getNewItemPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){


        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-item");

        modelAndView.addObject("user", user);


        modelAndView.addObject("createNewItem", CreateNewItem.builder().build());

        return modelAndView;
    }


    @PostMapping()
    public String createNewItem(@Valid CreateNewItem createNewItem, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (bindingResult.hasErrors()) {
            return "add-item";
        }


        User user = userService.getById(authenticationMetadata.getUserId());


        Item item = itemService.create(createNewItem, user);
        UUID itemId = item.getId();


        return "redirect:/items/my-closet";
    }



    @GetMapping("/my-closet")
    public ModelAndView getMyCloset(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.getById(authenticationMetadata.getUserId());

        List<Item> items = itemService.getAvailableItemsByOwner(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("my-closet");
        modelAndView.addObject("user", user);
        modelAndView.addObject("items", items);

        return modelAndView;
    }

    @GetMapping("/offers")
    public ModelAndView getItemsForOthers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());


        List<Item> itemsFromOthers = itemService.getItemsFromOthers(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("offers");
        modelAndView.addObject("user", user);
        modelAndView.addObject("itemsFromOthers", itemsFromOthers);

        return modelAndView;
    }

    @PostMapping("/rent/{itemId}")
    public String rentItem(@PathVariable UUID itemId, @AuthenticationPrincipal AuthenticationMetadata auth) {
        User borrower = userService.getById(auth.getUserId());

        // Обновява артикула и му закача borrower
        itemService.markAsBorrowed(itemId, borrower);

        return "redirect:/items/offers";
    }

}
