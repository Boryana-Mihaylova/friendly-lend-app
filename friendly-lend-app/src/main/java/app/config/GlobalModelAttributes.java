package app.config;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;

    @Autowired
    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User currentUser(@AuthenticationPrincipal AuthenticationMetadata auth) {
        return auth != null ? userService.getById(auth.getUserId()) : null;
    }
}
