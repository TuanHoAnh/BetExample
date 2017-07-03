package vn.kms.ngaythobet.service.competition;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.competition.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.*;

public class CompetitionServiceTest extends BaseTest {
    private CompetitionService competitionService;
    private static final String ALIAS_KEY = "alias";
    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private BettingGroupRepository bettingGroupRepository;

    @Override
    protected void doStartUp() {
        competitionService = new CompetitionService(competitionRepository,competitorRepository,matchRepository, bettingGroupRepository);
    }

    @Override
    protected void doTearDown() {
        matchRepository.deleteAll();
        competitorRepository.deleteAll();
        competitionRepository.deleteAll();
    }

    private Competition mockRawCompetition(Long id) {
        Competition competition = new Competition();
        competition.setId(id);
        competition.setAliasKey(ALIAS_KEY);
        competition.setName("name");
        competition.setLogo("logo");
        List<String> rounds = new ArrayList<>();
        rounds.add("1");
        rounds.add("2");
        competition.setRounds(rounds);
        return competition;
    }

    private Competitor mockRawCompetitor(Competition competition, String name)
    {
        Competitor competitor = new Competitor();
        competitor.setName(name);
        competitor.setLogo("logo");
        competitor.setCompetition(competition);
        return competitor;
    }

    private Match mockRawCreateMatch(Competition competition, String round, Competitor competitor1, Competitor competitor2){
        Match match = new Match();
        match.setCompetition(competition);
        match.setRound(round);
        match.setCompetitor1(competitor1);
        match.setCompetitor2(competitor2);
        match.setScore1(1);
        match.setScore2(2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("1986-04-08 12:30", formatter);
        match.setStartTime(dateTime);
        match.setLocation("TanSonNhat");

        return match;
    }

    @Test
    public void testSaveNewData() throws Exception {
        Competition competition = mockRawCompetition(null);
        competitionService.saveCompetition(competition);
        Competition newCompetition = competitionRepository.findOneByAliasKey(ALIAS_KEY).get();
        assertEquals(newCompetition.getName(), "name");
    }

    @Test
    public void testUpdateNameCompetition() throws Exception {
        Competition competition = mockRawCompetition(null);
        competitionRepository.save(competition);
        String newName = "new name";
        competition.setName(newName);
        competitionService.saveCompetition(competition);
        Competition updatedCompetition = competitionRepository.findOneByAliasKey(ALIAS_KEY).orElse(null);
        assertEquals(updatedCompetition.getName(), newName);
    }

    @Test
    public void testPublishCompetitionFail() throws Exception {
        Competition competition = mockRawCompetition(null);
        competitionRepository.save(competition);
        assertFalse(competitionService.publishCompetition(competition.getAliasKey()));
    }

    @Test
    public void testPublishCompetitionSuccessfully() throws Exception {
        Competition competition = mockRawCompetition(null);
        Competitor competitor1 = mockRawCompetitor(competition,"name1");
        Competitor competitor2 = mockRawCompetitor(competition,"name2");
        Match match = mockRawCreateMatch(competition,"1",competitor1,competitor2);
        competitionRepository.save(competition);
        competitorRepository.save(competitor1);
        competitorRepository.save(competitor2);
        matchRepository.save(match);
        assertTrue(competitionService.publishCompetition(competition.getAliasKey()));
    }

    @Test
    public void testFindByAliasKey() throws Exception {
        Competition competition = mockRawCompetition(null);
        competitionRepository.save(competition);
        assertNotNull(competitionService.findOneByAliasKey(ALIAS_KEY));
    }

    @Test
    public void testGetGroupCount() throws Exception {
        Competition competition = mockRawCompetition(null);
        competitionRepository.save(competition);
        assertEquals(competitionService.getGroupsCount(competition),new Integer(0));
    }
}

