package app.web.mapper;


import app.item.model.Item;
import app.item.service.ItemService;
import app.security.AuthenticationMetadata;
import app.survey.service.SurveyService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;


@Controller
public class IndexController {

    private final UserService userService;
    private final ItemService itemService;
    private final SurveyService surveyService;

    @Autowired
    public IndexController(UserService userService, ItemService itemService, SurveyService surveyService) {
        this.userService = userService;
        this.itemService = itemService;
        this.surveyService = surveyService;
    }


    @GetMapping("/")
    public String getIndexPage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/home";
        }

        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }

        return modelAndView;
    }




    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        List<Item> allItems = itemService.getAll();

        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);

        modelAndView.addObject("allItems", allItems);

        var unreadNotifications = userService.getUnreadNotifications(user.getId());
        modelAndView.addObject("unreadNotifications", unreadNotifications);

        if (user.getRole().name().equals("ADMIN")) {
            var surveyStats = surveyService.getSurveyStats();
            modelAndView.addObject("surveyStats", surveyStats);
        }

        return modelAndView;
    }

}
