package vn.kms.ngaythobet.domain.betting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.AsyncResult;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.service.betting.BettingGroupService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BettingGroupServiceTest extends BaseTest {
    private static final String TEST_GROUP_NAME = "testGroup";
    private static final String TEST_GROUP_NAME_2 = "testGroup2";

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CompetitionRepository competitionRepo;

    @Autowired
    private BettingGroupRepository bettingGroupRepo;

    @Autowired
    private CompetitorRepository competitorRepo;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private MailingService mailingService;


    private BettingGroupService bettingGroupService;

    @Override
    public void doStartUp() {
        Competition competition = new Competition();
        competition.setName("c1");
        competition.setId(1l);
        competition.setCompetitors(new ArrayList<Competitor>());
        competition.setStatus(Competition.Status.DRAFT);
        competition.setGroups(new ArrayList<BettingGroup>());
        competition.setLogo("logo.jpg");
        competition.setRounds(new ArrayList<String>());
        competition.setAliasKey("key123");

        competitionRepo.save(competition);
        MailingService mailService = mock(MailingService.class);
        when(mailService.sendEmailAsync(anyString(), anyString(), anyString()))
            .thenReturn(new AsyncResult<>(true));
        bettingGroupService = new BettingGroupService(bettingGroupRepo, mailService, userRepo);
    }

    @Override
    public void doTearDown() {
        bettingGroupRepo.deleteAll();
        userRepo.deleteAll();
        matchRepo.deleteAll();
        competitorRepo.deleteAll();
        competitionRepo.deleteAll();
    }


    @Test
    public void createModRequest_requestFromUser_saveAsPending() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("123");
        user.setRole(UserRole.USER);
        user.setEmail("testUser@gmail.com");
        user.setFirstName("user");
        user.setLastName("tester");
        user.setActivated(true);
        userRepo.save(user);

        bettingGroupService.createModRequest(user, TEST_GROUP_NAME, competitionRepo.findOneByName("c1").get());
        BettingGroup requestedBettingGroup = bettingGroupRepo.findOneByName(TEST_GROUP_NAME).get();
        assertThat(requestedBettingGroup.getStatus()).isEqualTo(BettingGroup.Status.PENDING);
    }

    @Test
    public void createModRequest_requestFromAdmin_saveAsDraft() throws Exception {
        User user = new User();
        user.setUsername("testAdmin");
        user.setPassword("123");
        user.setRole(UserRole.ADMIN);
        user.setEmail("testAdmin@gmail.com");
        user.setFirstName("admin");
        user.setLastName("tester");
        user.setActivated(true);
        userRepo.save(user);

        bettingGroupService.createModRequest(user, TEST_GROUP_NAME_2, competitionRepo.findOneByName("c1").get());
        BettingGroup requestedBettingGroup = bettingGroupRepo.findOneByName(TEST_GROUP_NAME_2).get();
        assertThat(requestedBettingGroup.getStatus()).isEqualTo(BettingGroup.Status.DRAFT);
    }

    @Test
    public void testCreateBettingGroup() {
        Competition competition = new Competition();
        competition.setId(1L);

        User moderator = new User();
        moderator.setId(2L);
        moderator.setUsername("testModerator");
        moderator.setPassword("User@123");
        moderator.setRole(UserRole.USER);
        moderator.setEmail("testModerator123@gmail.com");
        moderator.setFirstName("moderator");
        moderator.setLastName("tester");
        moderator.setActivated(true);
        userRepo.save(moderator);

        BettingGroup bettingGroup = new BettingGroup();
        bettingGroup.setName("GroupAAA");
        bettingGroup.setModerator(moderator);
        bettingGroup.setCompetition(competition);

        bettingGroupService.create(bettingGroup);

        BettingGroup bettingGroupResult = bettingGroupRepo.findOneByName("GroupAAA").get();

        assertThat(bettingGroup.getName().equals(bettingGroupResult.getName()));
    }

    @Test
    public void testReloadPendingEmailToUsername() {
        Competition competition = new Competition();
        competition.setId(1L);

        User moderator = new User();
        moderator.setId(2L);
        moderator.setUsername("testModerator");
        moderator.setPassword("Mod@123");
        moderator.setRole(UserRole.USER);
        moderator.setEmail("testModerator123@gmail.com");
        moderator.setFirstName("moderator");
        moderator.setLastName("tester");
        moderator.setActivated(true);
        userRepo.save(moderator);

        User user = new User();
        user.setId(3L);
        user.setUsername("testUsername");
        user.setPassword("User@123");
        user.setRole(UserRole.USER);
        user.setEmail("testUser1@gmail.com");
        user.setFirstName("User");
        user.setLastName("Name");
        user.setActivated(true);
        userRepo.save(user);

        BettingGroup bettingGroup = new BettingGroup();
        bettingGroup.setName("GroupAAA");
        bettingGroup.setModerator(moderator);
        bettingGroup.setCompetition(competition);
        bettingGroup.setPlayers(Arrays.asList("testModerator", "testUser1@gmail.com", "anonymousUser@gmail.com"));

        List<String> expectedPlayers = Arrays.asList("testModerator", "testUsername", "anonymousUser@gmail.com");

        assertThat(bettingGroupService.reloadPendingEmailToUsername(bettingGroup).getPlayers().equals(expectedPlayers));
    }

    @Test
    public void testCountingBettingGroupByStatus() {
        Integer count = bettingGroupService.countBettingGroupsByStatus(BettingGroup.Status.PENDING);
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void testDeleteRejectBettingGroup() {
        initData();

        Boolean result = false;

        Optional<BettingGroup> bettingGroup = bettingGroupRepo.findOneByName("Ngay tho bet 1");
        if(bettingGroup.isPresent()) {
            Long id = bettingGroup.get().getId();
            result = bettingGroupService.deleteRejectBettingGroup(id);
        }

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void testApproveBettingGroup() {
        initData();

        Boolean result = false;
        BettingGroup.Status status = null;

        Optional<BettingGroup> bettingGroup = bettingGroupRepo.findOneByName("Ngay tho bet 1");
        if(bettingGroup.isPresent()) {
            Long id = bettingGroup.get().getId();
            result = bettingGroupService.approveBettingGroup(id);
            status = bettingGroup.get().getStatus();
        }

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void testGetPendingBettingGroups() {
        initData();
        int pageSize = 10;

        Page<BettingGroup> page = bettingGroupService.getPendingBettingGroups(1, pageSize);

        assertThat(page.getTotalElements()).isEqualTo(1l);
    }

    public void initData() {
        User user = createRawUser();
        userRepo.save(user);

        Competition competition = createRawCompetition();
        competitionRepo.save(competition);

        BettingGroup bettingGroup = createRawBettingGroup(user, competition);
        bettingGroupRepo.save(bettingGroup);
    }

    public User createRawUser() {
        User user = new User();
        user.setUsername("happycase");
        user.setFirstName("Happy");
        user.setLastName("Life");
        user.setEmail("thangvtran@kms-technology.com");
        user.setRole(UserRole.USER);
        user.setLanguageTag("en");
        return user;
    }

    public Competition createRawCompetition() {
        Competition competition = new Competition();
        competition.setName("Brazil");
        competition.setStatus(Competition.Status.FINISHED);
        competition.setGroups(new ArrayList<BettingGroup>());
        competition.setLogo("img.jpg");
        competition.setRounds(new ArrayList<String>());
        competition.setAliasKey("lols");
        return competition;
    }

    public BettingGroup createRawBettingGroup(User user, Competition competition) {
        BettingGroup bettingGroup1 = new BettingGroup();
        bettingGroup1.setCompetition(competition);
        bettingGroup1.setModerator(user);
        bettingGroup1.setStatus(BettingGroup.Status.PENDING);
        bettingGroup1.setName("Ngay tho bet 1");
        return bettingGroup1;
    }
}
