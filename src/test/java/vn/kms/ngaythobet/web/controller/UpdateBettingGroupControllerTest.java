package vn.kms.ngaythobet.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
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
import vn.kms.ngaythobet.BaseControllerTest;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.web.form.BettingGroupEditorForm;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@ActiveProfiles("utest")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class UpdateBettingGroupControllerTest extends BaseControllerTest{

    private static final String FIRST_BETTING_GROUP_NAME = "ONE";
    private static final String SECOND_BETTING_GROUP_NAME = "TWO";
    private static boolean isSetUp = false;
    private Principal principal = () -> "admin";
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8"));
    @MockBean
    private AuthService authService;
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
    private MockMvc mockMvc;
    private User modUser;

    @Before
    public void setUp() throws Exception {
        User mockLoggedMod = new User();
        mockLoggedMod.setId(1L);
        mockLoggedMod.setFirstName("Vu");
        mockLoggedMod.setUsername("admin");
        Optional<User> newOptional = Optional.of(mockLoggedMod);


        this.mockMvc = webAppContextSetup(webApplicationContext)
            .build();
        if (isSetUp == true)
            doStartUp();

    }

    public void addNewBettingGroupWithMod(User mod, String name) {
        BettingGroup bettingGroup = new BettingGroup();
        bettingGroup.setCompetition(competitionRepository.findOneByName("WC2017").get());
        bettingGroup.setModerator(mod);
        bettingGroup.setName(name);
        bettingGroup.setStatus(BettingGroup.Status.DRAFT);

        bettingGroupRepo.save(bettingGroup);

    }

    @Transactional
    public void doStartUp() {

        isSetUp = true;
        mockLoginUser("admin", UserRole.ADMIN);
        modUser = userRepo.findOneByUsername("admin").get();
        Competition competition = new Competition();
        competition.setStatus(Competition.Status.PUBLISHED);
        competition.setName("LaLigasd");
        competition.setAliasKey("Laligas");
        competition.setLogo("/images/laliga.jpg");
        competitionRepository.save(competition);



    }


    protected void mockLoginUser(String username, UserRole role) {
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

    private BettingGroupEditorForm mockUpdateBetting() {
        modUser = userRepo.findOneByUsername("admin").get();
        BettingGroupEditorForm editorForm = new BettingGroupEditorForm();
        editorForm.setModerator(modUser.getUsername());
        editorForm.setId(bettingGroupRepo.findOneByName(FIRST_BETTING_GROUP_NAME).get().getId());
        editorForm.setGroupName(FIRST_BETTING_GROUP_NAME);
        editorForm.setRules("sdsds");
        editorForm.setPlayers(modUser.getUsername());
        editorForm.setStatus("DRAFT");
        return editorForm;
    }


    @Test
    public void testUpdateBettingGroup_Success() throws Exception {


        modUser = userRepo.findOneByUsername("admin").get();
        BettingGroupEditorForm editorForm = new BettingGroupEditorForm();
        editorForm.setModerator(modUser.getUsername());
        editorForm.setId(bettingGroupRepo.findOneByName(SECOND_BETTING_GROUP_NAME).get().getId());
        editorForm.setGroupName(SECOND_BETTING_GROUP_NAME);
        editorForm.setRules("sdsds");
        editorForm.setPlayers(modUser.getUsername());
        editorForm.setStatus("DRAFT");


        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
    }


    @Test
    public void testUpdateBettingGroup_GroupName_Empty_Fail() throws Exception {

        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setGroupName("");
        editorForm.setId(bettingGroupRepo.findOneByName(SECOND_BETTING_GROUP_NAME).get().getId());

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data[0].code").value("Pattern"))
            .andExpect(jsonPath("$.data[0].field").value("groupName"))
            .andExpect(jsonPath("$.data[1].code").value("NotEmpty"))
            .andExpect(jsonPath("$.data[1].field").value("groupName"));

    }

    @Test
    public void testUpdateBettingGroup_GroupName_WrongRegex_Fail() throws Exception {

        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setGroupName("gkhlg;");
        editorForm.setId(bettingGroupRepo.findOneByName(SECOND_BETTING_GROUP_NAME).get().getId());

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data[0].code").value("Pattern"))
            .andExpect(jsonPath("$.data[0].field").value("groupName"));

    }

    @Test
    public void testUpdateBettingGroup_GroupName_Existing_Fail() throws Exception {


        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setGroupName(SECOND_BETTING_GROUP_NAME);

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(csrf())
            .with(csrf().asHeader())
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data[0].code").value("ValidEditGroupName"))
            .andExpect(jsonPath("$.data[0].field").value("groupName"));

    }


    @Test
    public void testUpdateBettingGroup_EmptyPlayer_Fail() throws Exception {


        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setPlayers("");

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false));

    }

    @Test
    public void testUpdateBettingGroup_NotDeleteExistingPlayers_Fail() throws Exception {

        BettingGroup bettingGroup = bettingGroupRepo.findBettingGroupById(1L).get();


        User user = userRepo.findOneByUsername("admin").get();
        List<User> listPlayer = new ArrayList<User>();
        listPlayer.add(user);

        List<String> listStringPlayer = listPlayer
            .stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
        bettingGroup.setPlayers(listStringPlayer);


        bettingGroupRepo.save(bettingGroup);

        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setPlayers("admin");

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data[0].code").value("ValidListUpdateGroupPlayer"))
            .andExpect(jsonPath("$.data[0].field").value("players"));

    }

    @Test
    public void testUpdateBettingGroup_NotExistingPlayer_Fail() throws Exception {


        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setPlayers("asdsad");

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false));

    }

    @Test
    public void testUpdateBettingGroup_NotExistingModerator_Fail() throws Exception {


        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setModerator("");

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data[0].code").value("NotEmpty"))
            .andExpect(jsonPath("$.data[0].field").value("moderator"));


    }

    @Test
    public void testUpdateBettingGroup_WrongModerator_Fail() throws Exception {


        User newUser = new User();
        newUser.setActivated(true);
        newUser.setUsername("abcd");
        newUser.setFirstName("abcd");
        newUser.setLastName("def");
        newUser.setEmail("vu15882251@gmail.com");
        newUser.setRole(UserRole.USER);
        userRepo.save(newUser);


        addNewBettingGroupWithMod(userRepo.findOneByUsername("abcd").get(), "THREE");


        BettingGroupEditorForm editorForm = mockUpdateBetting();
        editorForm.setModerator("abcd");
        editorForm.setId(bettingGroupRepo.findOneByName("THREE").get().getId());

        this.mockMvc.perform(post("/bettingGroup/update-betting-group")
            .content(json(editorForm))
            .with(csrf())
            .with(csrf().asHeader())
            .contentType(contentType)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            })
        )

            .andDo(print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data[0].code").value("HaveModeratorRole"))
            .andExpect(jsonPath("$.data[0].field").value("id"));


    }


    protected String json(Object o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);

    }
}
