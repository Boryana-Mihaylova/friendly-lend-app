package app.survey;
import app.survey.client.SurveyClient;
import app.survey.client.dto.SurveyRequest;
import app.survey.client.dto.SurveyResponse;
import app.survey.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SurveyServiceTest {

    private SurveyClient surveyClient;
    private SurveyService surveyService;

    @BeforeEach
    void setUp() {
        surveyClient = Mockito.mock(SurveyClient.class);
        surveyService = new SurveyService(surveyClient);
    }

    @Test
    void testSubmitSurvey_ShouldReturnResponseBody() {
        SurveyRequest request = new SurveyRequest();
        SurveyResponse response = new SurveyResponse();

        when(surveyClient.submitSurvey(request)).thenReturn(ResponseEntity.ok(response));

        SurveyResponse result = surveyService.submitSurvey(request);

        assertThat(result).isEqualTo(response);
        verify(surveyClient).submitSurvey(request);
    }

    @Test
    void testGetSurvey_ShouldReturnResponseBody() {
        UUID userId = UUID.randomUUID();
        SurveyResponse response = new SurveyResponse();

        when(surveyClient.getSurvey(userId)).thenReturn(ResponseEntity.ok(response));

        SurveyResponse result = surveyService.getSurvey(userId);

        assertThat(result).isEqualTo(response);
        verify(surveyClient).getSurvey(userId);
    }

    @Test
    void testGetSurvey_WhenException_ShouldReturnNull() {
        UUID userId = UUID.randomUUID();

        when(surveyClient.getSurvey(userId)).thenThrow(new RuntimeException("Failed"));

        SurveyResponse result = surveyService.getSurvey(userId);

        assertThat(result).isNull();
        verify(surveyClient).getSurvey(userId);
    }

    @Test
    void testGetSurveyStats_ShouldReturnStatsMap() {
        Map<String, Long> stats = Map.of("ECO", 5L, "NEUTRAL", 2L);

        when(surveyClient.getSurveyStats()).thenReturn(ResponseEntity.ok(stats));

        Map<String, Long> result = surveyService.getSurveyStats();

        assertThat(result).isEqualTo(stats);
        verify(surveyClient).getSurveyStats();
    }


}
