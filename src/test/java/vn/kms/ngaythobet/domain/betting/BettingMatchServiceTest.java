package vn.kms.ngaythobet.domain.betting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.service.betting.BettingMatchService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BettingMatchServiceTest extends BaseTest {

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
    private BettingMatchRepository bettingMatchRepo;

    @Autowired
    private MailingService mailingService;

    private BettingMatchService bettingMatchService;

    private BettingMatch rawBettingMatch;

    @Override
    public void doStartUp() {
        MailingService mailService = mock(MailingService.class);
        when(mailService.sendEmailAsync(anyString(), anyString(), anyString()))
            .thenReturn(new AsyncResult<>(true));
        bettingMatchService = new BettingMatchService(bettingMatchRepo, bettingGroupRepo);
        rawBettingMatch = createBettingMatch();
    }

    @Override
    public void doTearDown() {
        bettingMatchRepo.deleteAll();
        bettingGroupRepo.deleteAll();
        userRepo.deleteAll();
        matchRepo.deleteAll();
        competitorRepo.deleteAll();
        competitionRepo.deleteAll();
    }

    @Test
    public void createBettingMatch_successfully(){
        bettingMatchService.createBettingMatch(rawBettingMatch, bettingGroupRepo.findAll().get(0).getId());
        assertThat(bettingMatchRepo.findAll().size()).isEqualTo(1);
    }

    @Test
    public void checkBettingMatchUserInput_SameMatchIdGroupIdExistBettingMatchId_True(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        Long matchId = bettingMatch.getMatch().getId();
        Long groupId = bettingMatch.getBettingGroup().getId();
        Long bettingMatchId = bettingMatchRepo.findAll().get(0).getId();

        assertThat(bettingMatchService.checkBettingMatchUserInput(matchId, groupId, bettingMatchId)).isTrue();
    }

    @Test
    public void checkBettingMatchUserInput_NotSameMatchIdGroupIdExistBettingMatchId_False(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        Long matchId = bettingMatch.getMatch().getId() + 1;
        Long groupId = bettingMatch.getBettingGroup().getId();
        Long bettingMatchId = bettingMatchRepo.findAll().get(0).getId();

        assertThat(bettingMatchService.checkBettingMatchUserInput(matchId, groupId, bettingMatchId)).isFalse();
    }

    @Test
    public void checkUniqueMatch_validMatchIdAndGroupId_True(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        Long matchId = bettingMatch.getMatch().getId() + 1;
        Long groupId = bettingMatch.getBettingGroup().getId();

        assertThat(bettingMatchService.checkUniqueMatch(matchId, groupId)).isTrue();
    }

    @Test
    public void checkUniqueMatch_invalidMatchIdAndGroupId_False(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        Long matchId = bettingMatch.getMatch().getId();
        Long groupId = bettingMatch.getBettingGroup().getId();

        assertThat(bettingMatchService.checkUniqueMatch(matchId, groupId)).isFalse();
    }

    @Test
    public void checkActivateBettingMatch_notActivated_true(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        bettingMatch.setActive(false);

        assertThat(bettingMatchService.checkActivateBettingMatch(bettingMatch.getId())).isTrue();
    }

    @Test
    public void checkActivateBettingMatch_activated_false(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        bettingMatch.setActive(true);

        assertThat(bettingMatchService.checkActivateBettingMatch(bettingMatch.getId())).isFalse();
    }

    @Test
    public void checkExpireTimeBettingMatch_notExpired_true(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        bettingMatch.setExpiryTime(LocalDateTime.now().plusDays(1l));
        bettingMatchRepo.save(bettingMatch);

        assertThat(bettingMatchService.checkExpireTimeBettingMatch(bettingMatch.getId())).isTrue();
    }

    @Test
    public void checkExpireTimeBettingMatch_expired_false(){
        bettingMatchRepo.save(rawBettingMatch);
        BettingMatch bettingMatch = bettingMatchRepo.findAll().get(0);
        bettingMatch.setExpiryTime(LocalDateTime.now().minusDays(1l));
        bettingMatchRepo.save(bettingMatch);

        assertThat(bettingMatchService.checkExpireTimeBettingMatch(bettingMatch.getId())).isFalse();
    }

    public BettingMatch createBettingMatch(){
        User user = createRawUser();
        userRepo.save(user);

        Competition competition = createRawCompetition();
        competitionRepo.save(competition);

        BettingGroup bettingGroup = createRawBettingGroup(user, competition);
        bettingGroupRepo.save(bettingGroup);

        List<Competitor> competitors = create2RawCompetitors(competition);
        Match match = createRawMatch(competition, competitors.get(0), competitors.get(1));
        matchRepo.save(match);

       return createRawBettingMatch(bettingGroup, match);
    }

    public User createRawUser(){
        User user = new User();
        user.setUsername("thangvtran");
        user.setFirstName("Thang");
        user.setLastName("Tran");
        user.setEmail("thangvtran@kms-technology.com");
        user.setRole(UserRole.USER);
        user.setLanguageTag("en");
        return user;
    }

    public Competition createRawCompetition(){
        Competition competition = new Competition();
        competition.setName("bodaonha");
        competition.setStatus(Competition.Status.FINISHED);
        competition.setGroups(new ArrayList<BettingGroup>());
        competition.setLogo("log11o1.jpg");
        competition.setRounds(new ArrayList<String>());
        competition.setAliasKey("keyxxxx");
        return competition;
    }

    public BettingGroup createRawBettingGroup(User user, Competition competition){
        BettingGroup bettingGroup1 = new BettingGroup();
        bettingGroup1.setId(1l);
        bettingGroup1.setCompetition(competition);
        bettingGroup1.setModerator(user);
        bettingGroup1.setStatus(BettingGroup.Status.PENDING);
        bettingGroup1.setName("Ngay tho bet 1");
        return bettingGroup1;
    }

    public Match createRawMatch(Competition competition, Competitor competitor1, Competitor competitor2) {
        Match match = new Match();
        match.setId(1l);
        match.setCompetition(competition);
        match.setCompetitor1(competitor1);
        match.setCompetitor2(competitor2);
        match.setLocation("Munich");
        match.setStartTime(LocalDateTime.now());
        match.setAwayTeamName("away");
        match.setHomeTeamName("home");
        match.setRound("1");
        return match;
    }

    public BettingMatch createRawBettingMatch(BettingGroup bettingGroup, Match match) {
        BettingMatch bettingMatch = new BettingMatch();
        bettingMatch.setId(1l);
        bettingMatch.setBettingGroup(bettingGroup);
        bettingMatch.setMatch(match);
        bettingMatch.setActive(true);
        bettingMatch.setBalance1(1.0);
        bettingMatch.setBalance2(2.5);
        bettingMatch.setExpiryTime(LocalDateTime.now());
        bettingMatch.setBettingAmount(new BigDecimal("25000"));
        return bettingMatch;
    }

    public List<Competitor> create2RawCompetitors(Competition competition) {
        List<Competitor> competitors = new ArrayList<>();

        Competitor competitor = new Competitor();
        competitor.setId(1l);
        competitor.setCompetition(competition);
        competitor.setName("competitor1");
        competitor.setLogo("logo1.jpg");
        competitors.add(competitor);
        competitorRepo.save(competitor);

        Competitor competitor2 = new Competitor();
        competitor2.setId(2l);
        competitor2.setCompetition(competition);
        competitor2.setName("competitor2");
        competitor2.setLogo("logo2.jpg");
        competitors.add(competitor2);
        competitorRepo.save(competitor2);
        return competitors;
    }
}
