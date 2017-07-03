package vn.kms.ngaythobet.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.Application;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.common.MessageService;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.service.competition.CompetitionService;
import vn.kms.ngaythobet.service.user.UserService;

import javax.servlet.Filter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@ActiveProfiles("utest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class PulishBettingGroupControllerTest {

    private static final String FIRST_BETTING_GROUP_NAME = "ONE";
    private static final String SECOND_BETTING_GROUP_NAME = "TWO";
    private static boolean isSetUp;
    private Principal principal = () -> "admin";
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8"));
    @MockBean
    private MailingService mailingService;
    @Autowired
    private UserRepository userRepo;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private BettingGroupRepository bettingGroupRepo;
    @Autowired
    private CompetitionRepository competitionRepository;
    @MockBean
    private MessageService messageService;
    @MockBean
    private CompetitionService competitionService;
    @MockBean
    private UserService userService;
    @Autowired
    private Filter springSecurityFilterChain;
    private MockMvc mockMvc;
    private User modUser;
    @MockBean
    private AuthService authService;

    @Before
    public void setUp() throws Exception {
        User mockLoggedMod = new User();
        mockLoggedMod.setId(1L);
        mockLoggedMod.setFirstName("Vu");
        mockLoggedMod.setUsername("admin");
        Optional<User> newOptional = Optional.of(mockLoggedMod);
        when(authService.getLoginUser())
            .thenReturn(newOptional);

        this.mockMvc = webAppContextSetup(webApplicationContext).addFilter(springSecurityFilterChain).build();
        if (isSetUp != true)
            doStartUp();

    }

    public void addNewBettingGroupWithMod(User mod, String name) {
        BettingGroup bettingGroup = new BettingGroup();
        bettingGroup.setCompetition(competitionRepository.findOneByName("LaLiga").get());
        bettingGroup.setModerator(mod);
        bettingGroup.setName(name);
        bettingGroup.setStatus(BettingGroup.Status.DRAFT);

        bettingGroupRepo.save(bettingGroup);

    }

    public void doStartUp() {

        isSetUp = true;
        mockLoginUser("admin", UserRole.ADMIN);

        modUser = userRepo.findOneByUsername("admin").get();

        Competition competition = new Competition();
        competition.setStatus(Competition.Status.PUBLISHED);
        competition.setName("LaLiga");
        competition.setAliasKey("Laliga");
        competition.setLogo("/images/laliga.jpg");
        competitionRepository.save(competition);


        addNewBettingGroupWithMod(modUser, FIRST_BETTING_GROUP_NAME);
        addNewBettingGroupWithMod(modUser, SECOND_BETTING_GROUP_NAME);


    }


    private void mockLoginUser(String username, UserRole role) {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }

            public String getFullName() {
                return username;
            }

            public LocalDate getRegisteredDate() {
                return LocalDate.MAX;
            }
        };
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, username, AuthorityUtils.createAuthorityList(role.getAuthority()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }


    @Test
    public void testUpdateBettingGroup_Success() throws Exception {


        modUser = userRepo.findOneByUsername("admin").get();
        this.mockMvc.perform(post("/bettingGroup/publish-betting-group")
            .param("id", "1")
            .with(csrf())
            .with(csrf().asHeader())
        )

            .andDo(print())
            .andExpect(redirectedUrl("/bettingGroup/group-detail?groupid=1&success=true"));

    }


    protected String json(Object o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);

    }
}
