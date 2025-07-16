package app.web;
import app.security.AuthenticationMetadata;
import app.survey.client.dto.SurveyRequest;
import app.survey.client.dto.SurveyResponse;
import app.survey.service.SurveyService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = app.web.mapper.SurveyController.class)
public class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyService surveyService;

    @MockBean
    private UserService userService;

    private UUID mockUserId;

    @BeforeEach
    void setup() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            mockUserId = UUID.randomUUID();
            AuthenticationMetadata metadata = new AuthenticationMetadata(
                    mockUserId,
                    "testuser",
                    "test@example.com",
                    UserRole.USER,
                    true
            );
            SecurityContextHolder.getContext().setAuthentication(
                    new TestingAuthenticationToken(metadata, null, "ROLE_USER")
            );
        }
    }

    @Test

    void testVoteSupport_ShouldSubmitSurveyAndReturnView() throws Exception {
        User user = new User();
        Mockito.when(userService.getById(mockUserId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/vote")
                        .param("subject", "Recycling")
                        .param("support", "High"))
                .andExpect(status().isOk())
                .andExpect(view().name("survey-confirmation"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attributeExists("user"));

        ArgumentCaptor<SurveyRequest> captor = ArgumentCaptor.forClass(SurveyRequest.class);
        verify(surveyService).submitSurvey(captor.capture());

        SurveyRequest sent = captor.getValue();
        assertThat(sent.getUserId()).isEqualTo(mockUserId);
        assertThat(sent.getSubject()).isEqualTo("Recycling");
        assertThat(sent.getSupport()).isEqualTo("High");
    }

    @Test

    void testGetSurvey_WithResponse_ShouldReturnModel() throws Exception {
        SurveyResponse response = new SurveyResponse(mockUserId, "Plastic", "Medium");
        when(surveyService.getSurvey(mockUserId)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/user-survey"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-survey"))
                .andExpect(model().attribute("surveyResponse", response))
                .andExpect(model().attribute("noSupportYet", false));
    }

    @Test

    void testGetSurvey_WithoutResponse_ShouldShowEmptyMessage() throws Exception {
        when(surveyService.getSurvey(mockUserId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/user-survey"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-survey"))
                .andExpect(model().attribute("noSupportYet", true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStats_AsAdmin_ShouldReturnJsonMap() throws Exception {
        Map<String, Long> stats = Map.of("Recycling", 5L, "Energy", 3L);
        when(surveyService.getSurveyStats()).thenReturn(stats);

        mockMvc.perform(MockMvcRequestBuilders.get("/surveys/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Recycling").value(5))
                .andExpect(jsonPath("$.Energy").value(3));
    }
}
