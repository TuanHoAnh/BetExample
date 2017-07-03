package vn.kms.ngaythobet.domain.statistic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.betting.*;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.service.betting.BettingGroupService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hungptnguyen on 4/26/2017.
 */
public class StatisticServiceTest extends BaseTest {

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
    private BettingMatchRepository bettingMatchRepository;

    @Autowired
    private BettingPlayerRepository bettingPlayerRepo;

    private StatisticService statisticService;

    public BettingMatch createBettingMatch(BigDecimal amount){
        User user = createRawUser("thanhhung","thanhhung94@gmail.com");
        userRepo.save(user);
        user = createRawUser("hungthanh","hungptnguyen@kms-technology.com");
        userRepo.save(user);

        Competition competition = createRawCompetition();
        competitionRepo.save(competition);

        BettingGroup bettingGroup = createRawBettingGroup(user, competition);
        bettingGroupRepo.save(bettingGroup);

        create2RawCompetitors(competition);
        Match match = createRawMatch("SG");
        matchRepo.save(match);

        return createRawBettingMatch(amount);
    }

    public User createRawUser(String userName, String email){
        User user = new User();

        user.setUsername(userName);
        user.setFirstName("Hung");
        user.setLastName("Nguyen");
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setLanguageTag("en");
        return user;
    }

    public Competition createRawCompetition(){
        Competition competition = new Competition();
        competition.setName("Sea game");
        competition.setStatus(Competition.Status.FINISHED);
        competition.setGroups(new ArrayList<BettingGroup>());
        competition.setLogo("log11o1.jpg");
        competition.setRounds(new ArrayList<String>());
        competition.setAliasKey("SG");
        return competition;
    }

    public BettingGroup createRawBettingGroup(User user, Competition competition){
        List<String> listUsername = new ArrayList<>();
        listUsername.add("thanhhung");
        listUsername.add("hungthanh");
        BettingGroup bettingGroup1 = new BettingGroup();
        bettingGroup1.setId(1L);
        bettingGroup1.setCompetition(competition);
        bettingGroup1.setModerator(user);
        bettingGroup1.setPlayers(listUsername);
        bettingGroup1.setStatus(BettingGroup.Status.PENDING);
        bettingGroup1.setName("NTB 1");
        return bettingGroup1;
    }

    public Match createRawMatch(String competitionKey) {
        Match match = new Match();
        match.setCompetition(competitionRepo.findOneByAliasKey(competitionKey).orElse(null));
        match.setCompetitor1(competitorRepo.findOneByNameAndCompetitionName("VietNam","Sea game").orElse(null));
        match.setCompetitor2(competitorRepo.findOneByNameAndCompetitionName("ThaiLan","Sea game").orElse(null));
        match.setLocation("Hue");
        match.setStartTime(LocalDateTime.now());
        match.setAwayTeamName("away");
        match.setHomeTeamName("home");
        match.setRound("Chung ket");

        //Score VietNam 1 : 0 ThaiLan
        match.setScore1(1);
        match.setScore2(0);

        return match;
    }

    public BettingMatch createRawBettingMatch(BigDecimal amount) {
        BettingMatch bettingMatch = new BettingMatch();
        bettingMatch.setBettingGroup(bettingGroupRepo.findBettingGroupById(1L).orElse(null));
        bettingMatch.setMatch(matchRepo.findByCompetitionAndRound(competitionRepo.findOneByAliasKey("SG").orElse(null),"Chung ket").get(0));
        bettingMatch.setActive(true);
        bettingMatch.setBalance1(1.0);
        bettingMatch.setBalance2(1.0);
        bettingMatch.setExpiryTime(LocalDateTime.now());
        bettingMatch.setBettingAmount(amount);
        return bettingMatch;
    }

    public List<Competitor> create2RawCompetitors(Competition competition) {
        List<Competitor> competitors = new ArrayList<>();

        Competitor competitor = new Competitor();
        competitor.setCompetition(competition);
        competitor.setName("VietNam");
        competitor.setLogo("logo1.jpg");
        competitors.add(competitor);
        competitorRepo.save(competitor);

        Competitor competitor2 = new Competitor();
        competitor2.setCompetition(competition);
        competitor2.setName("ThaiLan");
        competitor2.setLogo("logo2.jpg");
        competitors.add(competitor2);
        competitorRepo.save(competitor2);
        return competitors;
    }

    @Override
    public void doStartUp() {
        //create betting match 1: VietNam - ThaiLan
        BettingMatch rawBettingMatch = createBettingMatch(new BigDecimal(22000));
        bettingMatchRepository.save(rawBettingMatch);

        //player thanhhung bet VietNam
        BettingPlayer bettingPlayer1 = new BettingPlayer();
        bettingPlayer1.setPlayer(userRepo.findOneByUsername("thanhhung").orElse(null));
        bettingPlayer1.setBettingMatch(bettingMatchRepository.findAll().get(0));
        bettingPlayer1.setBetCompetitor(competitorRepo.findOneByNameAndCompetitionName("VietNam","Sea game").orElse(null));
        bettingPlayerRepo.save(bettingPlayer1);

        //player hungthanh bet ThaiLan
        BettingPlayer bettingPlayer2 = new BettingPlayer();
        bettingPlayer2.setPlayer(userRepo.findOneByUsername("hungthanh").orElse(null));
        bettingPlayer2.setBettingMatch(bettingMatchRepository.findAll().get(0));
        bettingPlayer2.setBetCompetitor(competitorRepo.findOneByNameAndCompetitionName("ThaiLan","Sea game").orElse(null));
        bettingPlayerRepo.save(bettingPlayer2);

        statisticService = new StatisticService(bettingMatchRepository,userRepo,bettingGroupRepo);
    }

    @Override
    @After
    public void tearDown() {
        bettingPlayerRepo.deleteAll();
        bettingMatchRepository.deleteAll();
        bettingGroupRepo.deleteAll();
        userRepo.deleteAll();
        matchRepo.deleteAll();
        competitorRepo.deleteAll();
        competitionRepo.deleteAll();
    }

    @Test
    @Transactional
    public void getListBettingPlayerInGroup() throws Exception {
        List<BettingPlayer> bettingPlayers = statisticService.getListBettingPlayerInGroup(1L);
        //player 1 bet VietNam then no lose
        assertThat(bettingPlayers.get(0).calculateLossAmount()).isEqualTo(new BigDecimal(0.0));
        //player 2 bet ThaiLan then lose
        assertThat(bettingPlayers.get(1).calculateLossAmount()).isEqualTo(new BigDecimal("22000.00"));

        Map<String, BigDecimal> totalLossAmount = statisticService.totalLossAmountByName(1L);
        assertThat(totalLossAmount.get("Hung \nNguyen")).isEqualTo(new BigDecimal("22000.00"));
    }
}
