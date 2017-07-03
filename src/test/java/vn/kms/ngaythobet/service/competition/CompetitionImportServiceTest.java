package vn.kms.ngaythobet.service.competition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.competition.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CompetitionImportServiceTest extends BaseTest{
    final String KEY = "424";
    final String NAME = "WORLD CUP";
    final String ALIAS_KEY = "123";
    final int NO_OF_MATCH_DAYS = 1;

    @Value("${ngaythobet.competitor-logo}")
    private String competitorDeafultLogo;

    Competition competition;
    Competitors competitors;
    Matches matches;
    CompetitionAPIData competitionAPIData;

    @Autowired
    CompetitionImportService competitionImportService;

    @Autowired
    CompetitionRepository competitionRepo;

    @Autowired
    CompetitorRepository competitorRepo;

    @Autowired
    MatchRepository matchRepo;

    private void prepare() {
        competitionAPIData = new CompetitionAPIData(NAME, ALIAS_KEY, KEY);
        Competitor competitor1 = new Competitor();
        Competitor competitor2 = new Competitor();
        Match match = new Match();
        Result result = new Result();

        competition = new Competition();
        competition.setName(NAME);
        competition.setAliasKey(ALIAS_KEY);
        competition.setNumberOfMatchdays(NO_OF_MATCH_DAYS);
        competition.setRounds(Arrays.asList(""));

        competitor1.setName("FRANCE");
        competitor1.setLogo("");
        competitor1.setCompetition(competition);

        competitor2.setName("USA");
        competitor2.setLogo("");
        competitor2.setCompetition(competition);

        result.setGoalsAwayTeam(1);
        result.setGoalsHomeTeam(1);

        match.setMatchday(1);
        match.setAwayTeamName("USA");
        match.setHomeTeamName("FRANCE");
        match.setStartTime(LocalDateTime.now());
        match.setResult(result);

        competitors = new Competitors();
        competitors.setCompetitors(Arrays.asList(competitor1, competitor2));

        matches = new Matches();
        matches.setMatches(Arrays.asList(match));

        competitionAPIData.setCompetition(competition);
        competitionAPIData.setCompetitors(competitors);
        competitionAPIData.setMatches(matches);
    }

    @Before
    public void doStartup() {
        matchRepo.deleteAll();
        competitorRepo.deleteAll();
        competitionRepo.deleteAll();
        prepare();
    }

    @After
    public void doTearDown() {
        matchRepo.deleteAll();
        competitorRepo.deleteAll();
        competitionRepo.deleteAll();
    }

    @Test
    public void testImportCompetition() throws Exception {
        Method importCompetition;
        Optional<Competition> competitionOptional;

        importCompetition = CompetitionImportService.class
            .getDeclaredMethod("importCompetition", CompetitionAPIData.class);
        importCompetition.setAccessible(true);
        importCompetition.invoke(competitionImportService, competitionAPIData);

        competitionOptional = competitionRepo.findOneByAliasKey(ALIAS_KEY);

        assertTrue(competitionOptional.isPresent());
    }

    @Test(expected = Exception.class)
    public void testImportCompetitionFail() throws Exception {
        Method importCompetition;
        Optional<Competition> competitionOptional;

        competition = competitionRepo.save(competition);
        competitionAPIData.getCompetition().setId(null);

        importCompetition = CompetitionImportService.class
            .getDeclaredMethod("importCompetition", CompetitionAPIData.class);
        importCompetition.setAccessible(true);
        importCompetition.invoke(competitionImportService, competitionAPIData);
    }

    @Test
    public void testImportCompetitors() throws Exception {
        Method importCompetitors;
        Optional<Competitor> competitorOptional;

        competition = competitionRepo.save(competition);
        importCompetitors = CompetitionImportService.class.getDeclaredMethod("importCompetitors", CompetitionAPIData.class);
        importCompetitors.setAccessible(true);
        importCompetitors.invoke(competitionImportService, competitionAPIData);
        competitorOptional = competitorRepo.findOneByNameAndCompetitionName(competitors.getCompetitors().get(0).getName(),
            competition.getName());

        assertTrue(competitorOptional.isPresent());
    }

    @Test
    public void testImportCompetitorsNullLogo() throws Exception {
        Method importCompetitors;
        Optional<Competitor> competitorOptional;

        competition = competitionRepo.save(competition);
        competitionAPIData.getCompetitors().getCompetitors().get(0).setLogo(null);
        importCompetitors = CompetitionImportService.class.getDeclaredMethod("importCompetitors", CompetitionAPIData.class);
        importCompetitors.setAccessible(true);
        importCompetitors.invoke(competitionImportService, competitionAPIData);
        competitorOptional = competitorRepo.findOneByNameAndCompetitionName(competitors.getCompetitors().get(0).getName(),
            competition.getName());

        assertTrue(competitorOptional.get().getLogo().equals(competitorDeafultLogo));
    }


    @Test
    public void testImportMatches() throws Exception {
        Method importMatches;
        Optional<Match> matchOptional;

        competition = competitionRepo.save(competition);
        for(Competitor competitor: competitors.getCompetitors()) {
            competitor.setCompetition(competition);
            competitor.setId(null);
            competitor = competitorRepo.save(competitor);
        }
        importMatches = CompetitionImportService.class.getDeclaredMethod("importMatches", CompetitionAPIData.class);
        importMatches.setAccessible(true);
        importMatches.invoke(competitionImportService, competitionAPIData);
        matchOptional = matchRepo.findOneMatch(matches.getMatches().get(0));
        assertTrue(matchOptional.isPresent());
    }

    @Test
    public void testDoImport() throws Exception {
        Iterable<Competitor> competitorIterable;
        Iterable<Competition> competitionIterable;
        Iterable<Match> matchIterable;

        competitionImportService.doImport(NAME, ALIAS_KEY, "424");
        competitionIterable = competitionRepo.findAll();
        competitorIterable = competitorRepo.findAll();
        matchIterable = matchRepo.findAll();

        assertTrue(competitionIterable.iterator().hasNext()
            && competitorIterable.iterator().hasNext()
            && matchIterable.iterator().hasNext());
    }

    @Test
    public void testGenerateRounds() throws Exception {
        Method generateRounds;
        Competition competition = new Competition();

        competition.setNumberOfMatchdays(2);
        generateRounds = CompetitionImportService.class.getDeclaredMethod("generateRounds", Competition.class);
        generateRounds.setAccessible(true);
        List<String> expected = Arrays.asList("1st.", "2nd.");
        List<String> actual = (List<String>) generateRounds.invoke(competitionImportService, competition);


        assertTrue(expected.get(0).equals(actual.get(0))
            && expected.get(1).equals(actual.get(1)));
    }

    @Test
    public void testRollbackWhenInterrupted() throws Exception {
        Competition newCompetition = competitionRepo.save(competition);
        Competitor newCompetitor1 = competitors.getCompetitors().get(0);
        Competitor newCompetitor2 = competitors.getCompetitors().get(1);
        Match newMatch = matches.getMatches().get(0);

        newCompetitor1.setId(null);
        newCompetitor2.setId(null);
        newCompetitor1.setCompetition(newCompetition);
        newCompetitor2.setCompetition(newCompetition);

        newCompetitor1 = competitorRepo.save(newCompetitor1);
        newCompetitor2 = competitorRepo.save(newCompetitor2);

        competitionImportService.rollbackWhenInterrupted(competition.getName());

        assertTrue(competitionRepo.findOne(newCompetition.getId()) == null
            && competitorRepo.findOne(newCompetitor1.getId()) == null
            && competitorRepo.findOne(newCompetitor2.getId()) == null);

    }

    @Test
    public void testRollbackWhenInterruptedNotFoundCompetition() throws Exception {
        final String competitionName = "!@$#$#";

        competitionImportService.rollbackWhenInterrupted(competitionName);
        assertTrue(!competitionRepo.findAll().stream().anyMatch(competition -> competition.getName().equals(competitionName)));

    }
}
