package vn.kms.ngaythobet.service.betting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.betting.*;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BettingPlayerServiceTest extends BaseTest {

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private BettingGroupRepository bettingGroupRepo;

    @Autowired
    private CompetitionRepository competitionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private BettingMatchRepository bettingMatchRepository;

    private BettingPlayerService bettingPlayerService;

    @Autowired
    private BettingPlayerRepository bettingPlayerRepository;

    public Competition createRawCompetition() {
        Competition competition = new Competition();
        competition.setName("bodaonha");
        competition.setStatus(Competition.Status.FINISHED);
        competition.setGroups(new ArrayList<BettingGroup>());
        competition.setLogo("log11o1.jpg");
        competition.setRounds(new ArrayList<String>());
        competition.setAliasKey("keyxxxx");
        return competition;
    }

    public BettingMatch createBettingMatch() {
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

    public BettingGroup createRawBettingGroup(User user, Competition competition) {
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
        competitorRepository.save(competitor);

        Competitor competitor2 = new Competitor();
        competitor2.setId(2l);
        competitor2.setCompetition(competition);
        competitor2.setName("competitor2");
        competitor2.setLogo("logo2.jpg");
        competitors.add(competitor2);
        competitorRepository.save(competitor2);
        return competitors;
    }

    public User createRawUser() {
        User user = new User();
        user.setUsername("thangvtran");
        user.setFirstName("Thang");
        user.setLastName("Tran");
        user.setEmail("thangvtran@kms-technology.com");
        user.setRole(UserRole.USER);
        user.setLanguageTag("en");
        return user;
    }

    @Override
    public void doStartUp() {
        bettingPlayerService = new BettingPlayerService(competitorRepository, bettingPlayerRepository, bettingMatchRepository);
    }

    @Override
    public void doTearDown() {
        competitorRepository.deleteAll();
        bettingPlayerRepository.deleteAll();
        bettingMatchRepository.deleteAll();
    }

    public BettingPlayer addBettingPlayer(Long bettingMatchId, Long betCompetitorId, User player) {
        Competitor betCompetitor = competitorRepository.findOne(betCompetitorId);
        BettingMatch bettingMatch = bettingMatchRepository.findOne(bettingMatchId);

        BettingPlayer bettingPlayer = new BettingPlayer();
        bettingPlayer.setBetCompetitor(betCompetitor);
        bettingPlayer.setBettingMatch(bettingMatch);
        bettingPlayer.setModifiedAt(LocalDateTime.now());
        bettingPlayer.setCreatedBy(bettingMatch.getBettingGroup().getModerator().getFullName());
        bettingPlayer.setPlayer(player);
        return bettingPlayerRepository.save(bettingPlayer);
    }

    @Test
    public void addBettingPlayer_validBettingMatchIdAndCompetitorIdAndUser_successfully() {
        BettingMatch bettingMatch = bettingMatchRepository.save(createBettingMatch());
        Long bettingMatchId = bettingMatch.getId();
        Long competitorId = bettingMatch.getMatch().getCompetitor1().getId();
        User user = bettingMatch.getBettingGroup().getModerator();
        bettingPlayerService.addBettingPlayer(bettingMatchId, competitorId, user);
        assertThat(bettingPlayerRepository.findAll().size() == 1);
    }

    @Test
    public void addBettingPlayer_validBettingMatchIdAndCompetitorIdAndUser_oldBettingPlayer_successfully() {
        BettingMatch bettingMatch = bettingMatchRepository.save(createBettingMatch());
        Long bettingMatchId = bettingMatch.getId();
        Long competitorId = bettingMatch.getMatch().getCompetitor1().getId();
        User user = bettingMatch.getBettingGroup().getModerator();
        BettingPlayer oldBettingPlayer = addBettingPlayer(bettingMatchId, competitorId, user);
        Long bettingMatchIdOldBettingPlayer = oldBettingPlayer.getBettingMatch().getId();
        Long competitorIdOldBettingPlayer = oldBettingPlayer.getBetCompetitor().getId();
        User userOldBettingPlayer = oldBettingPlayer.getPlayer();
        bettingPlayerService.addBettingPlayer(bettingMatchIdOldBettingPlayer, competitorIdOldBettingPlayer, userOldBettingPlayer);
        assertThat(bettingPlayerRepository.findAll().size() == 1);
    }

    @Test
    public void getSelectedCompetitor_validUserAndBettingMatch_successfully() {
        BettingMatch bettingMatch = bettingMatchRepository.save(createBettingMatch());
        Long bettingMatchId = bettingMatch.getId();
        Long competitorId = bettingMatch.getMatch().getCompetitor1().getId();
        User user = bettingMatch.getBettingGroup().getModerator();
        BettingPlayer bettingPlayer = addBettingPlayer(bettingMatchId, competitorId, user);
        bettingPlayer.setBetCompetitor(bettingMatch.getMatch().getCompetitor1());
        Competitor competitor = bettingPlayerService.getSelectedCompetitor(bettingPlayer.getPlayer(), bettingMatch);
        assertThat(competitor.getName() == "competitor1");
    }

    @Test
    public void getSelectedCompetitor_validUserAndBettingMatch_fail() {
        BettingMatch bettingMatch = bettingMatchRepository.save(createBettingMatch());
        Long bettingMatchId = bettingMatch.getId();
        Long competitorId = bettingMatch.getMatch().getCompetitor1().getId();
        User user = bettingMatch.getBettingGroup().getModerator();
        BettingPlayer bettingPlayer = addBettingPlayer(bettingMatchId, competitorId, user);
        bettingPlayer.setBetCompetitor(bettingMatch.getMatch().getCompetitor1());
        Competitor competitor = bettingPlayerService.getSelectedCompetitor(bettingPlayer.getPlayer(), bettingMatch);
        assertThat(competitor.getName() == "");
    }


}
