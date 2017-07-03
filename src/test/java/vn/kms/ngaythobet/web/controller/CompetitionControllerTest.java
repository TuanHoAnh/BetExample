package vn.kms.ngaythobet.web.controller;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.Application;
import vn.kms.ngaythobet.BaseControllerTest;


import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.domain.user.Language;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.service.competition.CompetitionService;
import vn.kms.ngaythobet.service.competition.MatchService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.form.CompetitionForm;
import vn.kms.ngaythobet.web.form.RegistrationForm;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class CompetitionControllerTest extends BaseTest{
    private static final String COMPETITION_NAME = "WORLD CUP";
    private static final String COMPETITION_ALIASKEY = "alias";

    @MockBean
    private UserService userService;

    @MockBean
    private MatchService matchService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private WebApplicationContext appContext;

    @MockBean
    private AuthService authService;

    protected MockMvc mockMvc;

    public User getUser(UserRole role){
        User user = new User();
        user.setUsername("admin");
        user.setPassword("");
        user.setRole(role);
        user.setAvatar("avatar.png");
        user.setEmail("testUser@gmail.com");
        user.setFirstName("user");
        user.setLastName("tester");
        user.setActivated(true);
        return user;
    }

    @Before
    public void startUp() {
        LocaleContextHolder.setLocale(new Locale("en"));
        mockLoginUser(getUser(UserRole.ADMIN));
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();
        doStartUp();
    }

    @Override
    protected void doStartUp() {
        when(userService.getAllowedLanguages(any()))
            .thenReturn(Arrays.asList(new Language("vi", "Vietnamese"), new Language("en", "English")));
    }

    @Test
    public void viewCompetition_CompetitionIdFound() throws Exception{
        competitionRepository.save(competition());
        User user = getUser(UserRole.ADMIN);
        when(authService.getLoginUser()).thenReturn(Optional.of(user));
        this.mockMvc.perform(get("/competitions/{aliasKey}", COMPETITION_ALIASKEY))
            .andExpect(status().isOk())
            .andExpect(view().name("competition/view"));
    }

    @Test
    public void testViewCompetitionPage() throws Exception{
        this.mockMvc.perform(get("/competitions/create-form"))
            .andExpect(status().isOk())
            .andExpect(view().name("competition-page"));
    }

    @Test
    public void testViewCompetitionUpdatePage() throws Exception{
        competitionRepository.save(competition());
        this.mockMvc.perform(get("/competitions/{aliasKey}/update",competition().getAliasKey()))
            .andExpect(status().isOk())
            .andExpect(view().name("competition-page"))
            .andExpect(model().attribute("competitionForm",
                    hasProperty("aliasKey", is(COMPETITION_ALIASKEY))))
            .andExpect(model().attribute("competitionForm",
                hasProperty("name", is(COMPETITION_NAME))));

    }

    @Test
    public void testCreateCompetitionSuccessfully() throws Exception{
        CompetitionForm competitionForm = new CompetitionForm();
        competitionForm.setId("");
        competitionForm.setAliasKey("key1");
        competitionForm.setName("name1");
        List<String> rounds = new ArrayList<>();
        rounds.add("1");
        competitionForm.setRounds(rounds);
        this.mockMvc.perform(postCompetitionForm(competitionForm))
            .andExpect(status().is(200));
    }

    @Test
    public void testCreateCompetitionFail() throws Exception{
        CompetitionForm competitionForm = new CompetitionForm();
        competitionForm.setId("");
        competitionForm.setAliasKey(null);
        competitionForm.setName(null);
        List<String> rounds = null;
        competitionForm.setRounds(rounds);
        this.mockMvc.perform(postCompetitionForm(competitionForm))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @Ignore
    public void viewCompetition_CompetitionIdNotFound() throws Exception{
        this.mockMvc.perform(get("/competitions/{aliasKey}", "WC"))
            .andExpect(status().isOk())
            .andExpect(view().name("500"));
    }


    private Match mockRawCreateMatch(Competition competition){
        Match match = new Match();
        match.setId(1L);
        match.setCompetition(competition);
        match.setCompetitor1(competitor1());
        match.setCompetitor2(competitor2());
        String str = "1986-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        match.setStartTime(dateTime);
        match.setLocation("VN");

        return match;
    }

    private Competition competition(){
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setName(COMPETITION_NAME);
        competition.setAliasKey(COMPETITION_ALIASKEY);
        return competition;
    }

    private Competitor competitor1(){
        Competitor competitor=new Competitor();

        competitor.setId(1L);
        competitor.setName("Germany");
        competitor.setLogo("germany.png");
        competitor.setCompetition(competition());

        return competitor;
    }

    private Competitor competitor2(){
        Competitor competitor=new Competitor();

        competitor.setId(2L);
        competitor.setName("Japan");
        competitor.setLogo("japan.png");
        competitor.setCompetition(competition());

        return competitor;
    }

    public String convertListRoundToString(List<String> items) {
        if (CollectionUtils.isEmpty(items)) {
            return "";
        }

        return String.join(",", items);
    }

    private MockHttpServletRequestBuilder postCompetitionForm(@Valid CompetitionForm competitionForm) {
        return post("/competitions/save")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("id",competitionForm.getId())
            .param("aliasKey", competitionForm.getAliasKey())
            .param("name", competitionForm.getName())
            .param("rounds", convertListRoundToString(competitionForm.getRounds()));
    }

}
