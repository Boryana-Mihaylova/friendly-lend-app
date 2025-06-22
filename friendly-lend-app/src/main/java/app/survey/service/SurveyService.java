package app.survey.service;


import app.survey.client.SurveyClient;
import app.survey.client.dto.SurveyRequest;
import app.survey.client.dto.SurveyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
public class SurveyService {

    private final SurveyClient surveyClient;


    @Autowired
    public SurveyService(SurveyClient surveyClient) {
        this.surveyClient = surveyClient;
    }



    public SurveyResponse submitSurvey(SurveyRequest surveyRequest) {
        ResponseEntity<SurveyResponse> httpResponse = surveyClient.submitSurvey(
                surveyRequest.getSubject(),
                surveyRequest.getSupport(),
                surveyRequest.getUserId()
        );
        return httpResponse.getBody();
    }

    public SurveyResponse getSurvey(UUID userId) {

        ResponseEntity<SurveyResponse> httpResponse = surveyClient.getSurvey(userId);
        return httpResponse.getBody();
    }

}
