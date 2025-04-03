package app.survey.client;


import app.survey.client.dto.SurveyRequest;
import app.survey.client.dto.SurveyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


@FeignClient(name = "svc-demo", url = "http://localhost:8081/api/v1/surveys")
public interface SurveyClient {



    @PostMapping
    ResponseEntity<SurveyResponse> submitSurvey(@RequestBody SurveyRequest SurveyRequest);


    @GetMapping("/user-survey")
    ResponseEntity<SurveyResponse> getSurvey(@RequestParam(name = "userId") UUID userId);
}









