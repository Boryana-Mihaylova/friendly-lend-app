package app.web.mapper;


import app.security.AuthenticationMetadata;
import app.survey.client.dto.SurveyRequest;
import app.survey.client.dto.SurveyResponse;
import app.survey.service.SurveyService;
import app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.UUID;

@Slf4j
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


    @GetMapping("/vote")
    public ModelAndView voteSupport(
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
            @RequestParam("subject") String subject,
            @RequestParam("support") String support) {

        UUID userId = authenticationMetadata.getUserId();

        log.info("Received vote from user {} for subject {} with support {}", userId, subject, support);

        SurveyRequest surveyRequest = new SurveyRequest(userId, subject, support);

        try {
            surveyService.submitSurvey(surveyRequest);
        } catch (Exception e) {
            log.error("Failed to submit survey: {}", e.getMessage(), e);
            throw e;
        }

        ModelAndView modelAndView = new ModelAndView("survey-confirmation");
        modelAndView.addObject("subject", subject);
        modelAndView.addObject("user", userService.getById(userId));
        return modelAndView;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(surveyService.getSurveyStats());
    }
}




