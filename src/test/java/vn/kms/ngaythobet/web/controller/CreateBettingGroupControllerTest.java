/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.controller;

import org.apache.tomcat.util.descriptor.LocalResolver;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.LocaleResolver;
import vn.kms.ngaythobet.BaseControllerTest;
import vn.kms.ngaythobet.config.AppProperties;
import vn.kms.ngaythobet.domain.betting.BettingMatchRepository;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.common.MessageService;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.statistic.StatisticService;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.service.betting.BettingGroupService;
import vn.kms.ngaythobet.service.competition.CompetitionService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.form.CreateBettingGroupForm;
import vn.kms.ngaythobet.web.interceptor.PropertiesInterceptor;

import javax.persistence.EntityManagerFactory;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BettingGroupController.class)
public class CreateBettingGroupControllerTest extends BaseControllerTest {

    private static final String CREATE_BETTING_GROUP_FORM = "content/create-betting-group-form :: create-betting-group";

    @MockBean
    private BettingGroupService bettingGroupService;

    @MockBean
    private CompetitionService competitionService;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private MailingService mailingService;

    @MockBean
    private AuthService authService;

    @MockBean
    private CompetitionRepository competitionRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private StatisticService statisticService;

    @MockBean
    private BettingMatchRepository bettingMatchRepository;

    @MockBean
    private AppProperties appProperties;

    @MockBean
    private PropertiesInterceptor propertiesInterceptor;

    @MockBean
    private LocaleResolver localeResolver;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @Override
    protected void doStartUp() {
        mockLoginUser(getUser(UserRole.USER));

    }

    @Test
    public void testCreateBettingGroupEmptyName() throws Exception {
        this.mockMvc.perform(postCreateBettingForm("", "1", "1")
            .with(csrf())
            .with(csrf().asHeader()))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1));
    }

    private MockHttpServletRequestBuilder postCreateBettingForm(String name, String moderatorId, String competitionAliasKey) {
        CreateBettingGroupForm createBettingGroupForm = new CreateBettingGroupForm();
        return post("/competitions/"+competitionAliasKey+"/bettings/create")
            .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("competitionAliasKey", competitionAliasKey)
            .param("moderatorid", moderatorId)
            .param("name", name);
    }

    @Test
    public void testViewBettingGroup() throws Exception {
        this.mockMvc.perform(get("/competitions/WC2017/bettings/create"))
            .andExpect(status().is(200))
            .andExpect(view().name(CREATE_BETTING_GROUP_FORM));
    }
}
