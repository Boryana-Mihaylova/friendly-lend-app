package app.web.mapper;


import app.favorite.model.Favorite;
import app.favorite.service.FavoriteService;
import app.item.model.Item;
import app.item.service.ItemService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final UserService userService;
    private final FavoriteService favoriteService;
    private final ItemService itemService;



    @Autowired
    public FavoriteController(UserService userService, FavoriteService favoriteService, ItemService itemService) {
        this.userService = userService;
        this.favoriteService = favoriteService;


        this.itemService = itemService;
    }


    @GetMapping("/add-to-favorite/{id}")
    public ModelAndView getNewFavorite(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());


        Item item = itemService.getItemById(id);

        CreateFavorite createFavorite = itemService.convertToItemFavorite(item);

        favoriteService.createFavoriteItem(createFavorite, user);


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("my-favorite");
        modelAndView.addObject("createFavorite", createFavorite);
        modelAndView.addObject("user", user);

        return modelAndView;

    }

    @GetMapping
    public ModelAndView getFavorites(@AuthenticationPrincipal AuthenticationMetadata auth) {
        User user = userService.getById(auth.getUserId());
        List<Favorite> favorites = favoriteService.getFavoritesByUser(user);

        ModelAndView modelAndView = new ModelAndView("favorites");
        modelAndView.addObject("favorites", favorites);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/remove/{id}")
    public String removeFavorite(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata auth) {
        User user = userService.getById(auth.getUserId());
        favoriteService.removeFavoriteById(id, user);
        return "redirect:/favorites";
    }
}
