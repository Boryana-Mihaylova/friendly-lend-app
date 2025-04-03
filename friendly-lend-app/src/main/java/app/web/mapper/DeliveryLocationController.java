package app.web.mapper;

import app.deliveryLocation.model.DeliveryLocation;
import app.deliveryLocation.servise.DeliveryLocationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LocationEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/locations")
public class DeliveryLocationController {

    private final UserService userService;

    private final DeliveryLocationService pickupLocationService;

    @Autowired
    public DeliveryLocationController(UserService userService, DeliveryLocationService pickupLocationService) {
        this.userService = userService;

        this.pickupLocationService = pickupLocationService;
    }

    @GetMapping("/edit")
    public ModelAndView getNewPickupLocationPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-location");

        modelAndView.addObject("user", user);

        modelAndView.addObject("locationEditRequest", LocationEditRequest.builder().build());

        return modelAndView;
    }

    @PostMapping()
    public String editPickupLocation(@Valid LocationEditRequest locationEditRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            return "edit-location";
        }


        User user = userService.getById(authenticationMetadata.getUserId());


        DeliveryLocation pickupLocation = pickupLocationService.edit(locationEditRequest, user);
        UUID itemId = pickupLocation.getId();


        return "redirect:/locations/my-location";
    }

    @GetMapping("/my-location")
    public ModelAndView getMyLocations(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.getById(authenticationMetadata.getUserId());

        List<DeliveryLocation> locations = pickupLocationService.getAllByOwnerId(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("my-location");
        modelAndView.addObject("user", user);
        modelAndView.addObject("locations", locations);

        return modelAndView;
    }

}
