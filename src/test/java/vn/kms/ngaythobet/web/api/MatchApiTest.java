package vn.kms.ngaythobet.web.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.auth.In;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.Application;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.service.competition.MatchService;
import vn.kms.ngaythobet.web.form.UpdateMatchScoreForm;

import javax.servlet.Filter;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class MatchApiTest extends BaseTest {

    @Autowired
    private WebApplicationContext appContext;

    protected MockMvc mockMvc;

    @MockBean
    private MatchRepository matchRepository;

    @MockBean
    private MatchService matchService;

    @Override
    protected void doStartUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();
    }

    @Test
    public void getMatch_Success() throws Exception {
        Long matchId = 1L;
        when(matchRepository.findOne(matchId)).thenReturn(new Match());
        this.mockMvc.perform(get("/api/match/{matchId}", matchId).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)));

        verify(matchRepository, times(1)).findOne(matchId);
        verifyNoMoreInteractions(matchRepository);
    }

    @Test
    public void getMatch_Fail() throws Exception {
        Long matchId = 23L;
        when(matchRepository.findOne(matchId)).thenReturn(null);
        this.mockMvc.perform(get("/api/match/{matchId}", matchId).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(false)));

        verify(matchRepository, times(1)).findOne(matchId);
        verifyNoMoreInteractions(matchRepository);
    }

    @Test
    public void updateMatchScore_Success() throws Exception {
        Long matchId = 1L;
        Integer score1 = 10;
        Integer score2 = 2;

        UpdateMatchScoreForm updateMatchScoreForm = new UpdateMatchScoreForm();
        updateMatchScoreForm.setScore1(score1);
        updateMatchScoreForm.setScore2(score2);

        Match match = new Match();
        BeanUtils.copyProperties(updateMatchScoreForm, match);

        when(matchService.updateMatchScore(matchId, match)).thenReturn(true);

        this.mockMvc.perform(put("/api/match/update-match-score/" + matchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(updateMatchScoreForm))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)));

        verify(matchService, times(1)).updateMatchScore(any(), any());
        verifyNoMoreInteractions(matchRepository);
    }

    @Test
    public void updateMatchScore_Fail_Update() throws Exception {
        Long matchId = 1231L;
        Integer score1 = 10234;
        Integer score2 = 22123;

        UpdateMatchScoreForm updateMatchScoreForm = new UpdateMatchScoreForm();
        updateMatchScoreForm.setScore1(score1);
        updateMatchScoreForm.setScore2(score2);

        Match match = new Match();
        BeanUtils.copyProperties(updateMatchScoreForm, match);

        when(matchService.updateMatchScore(matchId, match)).thenReturn(false);

        this.mockMvc.perform(put("/api/match/update-match-score/" + matchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(updateMatchScoreForm))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(false)));

        verify(matchService, times(1)).updateMatchScore(any(), any());
        verifyNoMoreInteractions(matchRepository);
    }

    @Test
    public void updateMatchScore_Fail_Validate() throws Exception {
        Long matchId = 1231L;
        Integer score1 = -10;
        Integer score2 = 22123;

        UpdateMatchScoreForm updateMatchScoreForm = new UpdateMatchScoreForm();
        updateMatchScoreForm.setScore1(score1);
        updateMatchScoreForm.setScore2(score2);

        Match match = new Match();
        BeanUtils.copyProperties(updateMatchScoreForm, match);

        when(matchService.updateMatchScore(matchId, match)).thenReturn(false);

        this.mockMvc.perform(put("/api/match/update-match-score/" + matchId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(updateMatchScoreForm))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(false)));

        verify(matchService, times(0)).updateMatchScore(any(), any());
        verifyNoMoreInteractions(matchRepository);
    }

    private String toJson(Object object) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);

    }

}
