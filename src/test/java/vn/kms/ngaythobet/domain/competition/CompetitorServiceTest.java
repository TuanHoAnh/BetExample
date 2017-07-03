package vn.kms.ngaythobet.domain.competition;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.CompetitorRepository;
import vn.kms.ngaythobet.service.competition.CompetitionService;
import vn.kms.ngaythobet.service.competition.CompetitorService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by phuvha on 4/17/2017.
 */
public class CompetitorServiceTest extends BaseTest {
    @Test
    public void saveCompetitor() throws Exception {

    }

    private CompetitionService competitionService;

    @Autowired
    private CompetitionRepository competitionRepository;

    private CompetitorService competitorService;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private BettingGroupRepository bettingGroupRepository;

    @Override
    protected void doStartUp() {
        competitionService = new CompetitionService(competitionRepository, competitorRepository, matchRepository, bettingGroupRepository);
        competitorService = new CompetitorService(competitorRepository, competitionService);
    }

    @Override
    protected void doTearDown() {
    }

    private Competition mockRawCompetition(Long id) {
        Competition competition = new Competition();
        competition.setId(id);
        competition.setAliasKey("key");
        competition.setName("name");
        competition.setLogo("logo");
        List<String> rounds = new ArrayList<>();
        rounds.add("1");
        rounds.add("2");
        competition.setRounds(rounds);
        return competition;
    }
    private Competitor mockRawCompetitor(Competition competition, Long id)
    {
        Competitor competitor = new Competitor();
        competitor.setName("competitor1");
        competitor.setLogo("logo1");
        competitor.setCompetition(competition);
        return competitor;
    }


    @Test
    public void testSaveNewData() throws Exception {
        Competition competition = mockRawCompetition(null);
        competitionService.saveCompetition(competition);
        Competitor competitor = mockRawCompetitor(competition, null);
        competitorService.saveCompetitor(competitor);
        Competitor resultCompetitor = competitorRepository.findOneByNameAndCompetitionName("competitor1","name").get();
        assertEquals(resultCompetitor.getLogo(),"logo1");

    }



}
