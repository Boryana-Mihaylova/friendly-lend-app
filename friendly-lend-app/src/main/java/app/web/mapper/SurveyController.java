package app.web.mapper;


import app.security.AuthenticationMetadata;
import app.survey.client.dto.SurveyRequest;
import app.survey.client.dto.SurveyResponse;
import app.survey.service.SurveyService;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


@Controller
@RequestMapping("/surveys")
public class SurveyController {

    private final UserService userService;
    private final SurveyService surveyService;

    @Autowired
    public SurveyController(UserService userService, SurveyService surveyService) {
        this.userService = userService;
        this.surveyService = surveyService;
    }



    @GetMapping("/newSurvey")
    public ModelAndView getNewSurveyPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user-support");
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("surveyRequest", SurveyRequest.builder().build());

        return modelAndView;
    }


    @PostMapping
    public String submitSurvey(@RequestParam("subject") String subject,
                                     @RequestParam("support") String support, @RequestParam("userId") UUID userId,
                                     @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        SurveyRequest surveyRequest = new SurveyRequest();
        surveyRequest.setSubject(subject);
        surveyRequest.setSupport(support);
        surveyRequest.setUserId(userId);


        surveyService.submitSurvey(surveyRequest);

        return "redirect:/surveys/user-survey";

    }


    @GetMapping("/user-survey")
    public ModelAndView getSurvey(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();

        SurveyResponse surveyResponse = surveyService.getSurvey(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user-survey");
        modelAndView.addObject("userId", userId);
        modelAndView.addObject("surveyResponse", surveyResponse);

        return modelAndView;
    }
}




