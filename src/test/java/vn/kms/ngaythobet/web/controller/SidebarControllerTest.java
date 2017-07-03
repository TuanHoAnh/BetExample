package vn.kms.ngaythobet.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.Application;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.service.competition.CompetitionImportService;

import javax.servlet.Filter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class SidebarControllerTest extends BaseTest{
    @MockBean
    CompetitionRepository competitionRepo;

    @Autowired
    private WebApplicationContext appContext;

    @Autowired
    private Filter springSecurityFilterChain;

    protected MockMvc mockMvc;

    List<Competition> competitionList = new ArrayList<>();

    @Override
    protected void doStartUp() {
        LocaleContextHolder.setLocale(new Locale("en"));
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();
        mockLoginUser("user");

        Competition competition;

        competition = new Competition();
        competition.setAliasKey("wc");
        competitionList.add(competition);

        competition = new Competition();
        competition.setAliasKey("wdfsve");
        competitionList.add(competition);

        competition = new Competition();
        competition.setAliasKey("fefw");
        competitionList.add(competition);

        competition = new Competition();
        competition.setAliasKey("wfweve");
        competitionList.add(competition);

        competition = new Competition();
        competition.setAliasKey("uihui");
        competitionList.add(competition);

        competition = new Competition();
        competition.setAliasKey("wq23gwfe");
        competitionList.add(competition);

        competition = new Competition();
        competition.setAliasKey("jhihuihy");
        competitionList.add(competition);

        when(competitionRepo.findAllCompetitionWithOrder()).thenReturn(competitionList);
    }

    @Test
    public void reloadSidebar_Active() throws Exception {
        String hashURI = "#/competitions/wfweve";
        this.mockMvc.perform(get("/sidebar")
            .param("hashUrl", hashURI))
            .andExpect(status().isOk())
            .andExpect(model().attribute("active", "wfweve"))
            .andExpect(model().attribute("competitions", competitionList))
            .andExpect(view().name("fragments/sidebar :: sidebar-competitions"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void reloadSidebar_NoActive1() throws Exception {
        String hashURI = "/competitions/asdsasasca";
        this.mockMvc.perform(get("/sidebar")
            .param("hashUrl", hashURI))
            .andExpect(status().isOk())
            .andExpect(model().attribute("active", ""))
            .andExpect(model().attribute("competitions", competitionList))
            .andExpect(view().name("fragments/sidebar :: sidebar-competitions"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void reloadSidebar_NoActive2() throws Exception {
        String hashURI = "/competitions/asd2wq";
        this.mockMvc.perform(get("/sidebar")
            .param("hashUrl", hashURI))
            .andExpect(status().isOk())
            .andExpect(model().attribute("active", ""))
            .andExpect(model().attribute("competitions", competitionList))
            .andExpect(view().name("fragments/sidebar :: sidebar-competitions"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void reloadSidebar_NoActive3() throws Exception {
        String hashURI = "/competitions/!21";
        this.mockMvc.perform(get("/sidebar")
            .param("hashUrl", hashURI))
            .andExpect(status().isOk())
            .andExpect(model().attribute("active", ""))
            .andExpect(model().attribute("competitions", competitionList))
            .andExpect(view().name("fragments/sidebar :: sidebar-competitions"));
    }

}
