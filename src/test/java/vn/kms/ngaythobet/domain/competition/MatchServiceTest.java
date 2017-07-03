package vn.kms.ngaythobet.domain.competition;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.service.competition.MatchService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class MatchServiceTest extends BaseTest{

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private CompetitionRepository competitionRepo;

    @Autowired
    private CompetitorRepository competitorRepo;

    @Override
    protected  void doStartUp(){
        matchRepo.deleteAll();
        competitorRepo.deleteAll();
        competitionRepo.deleteAll();
    }

    private Competition mockRawCompetition(String name, List<String> rounds){
        Competition competition = new Competition();
        competition.setId(null);
        competition.setName(name);
        competition.setAliasKey(name + "-key");
        competition.setLogo(name + "-logo");
        competition.setRounds(rounds);

        return competition;
    }

    private Competitor mockRawCompetitor(String name, Competition competition){
        Competitor competitor = new Competitor();
        competitor.setId(null);
        competitor.setName(name);
        competitor.setLogo(name + "-logo");
        competitor.setCompetition(competition);

        return competitor;
    }

    private Match mockRawCreateMatch(Competition competition, String round, Competitor competitor1, Competitor competitor2){
        Match match = new Match();
        match.setId(null);
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
    public void saveMatch_Success() throws Exception {
        List<String> rounds = new ArrayList<>();
        rounds.add("tu ket");

        Competition rawCompetition = mockRawCompetition("WC", rounds);
        competitionRepo.save(rawCompetition);

        Competitor rawCompetitor1 = mockRawCompetitor("11", rawCompetition);
        Competitor rawCompetitor2 = mockRawCompetitor("22", rawCompetition);
        competitorRepo.save(rawCompetitor1);
        competitorRepo.save(rawCompetitor2);

        Competitor competitor1 = competitorRepo.findOneByNameAndCompetitionName("11", "WC").orElse(null);
        Competitor competitor2 = competitorRepo.findOneByNameAndCompetitionName("22", "WC").orElse(null);
        Match rawMatch = mockRawCreateMatch(rawCompetition, "tu ket", competitor1, competitor2);
        matchService.saveMatch(rawMatch);
    }


    @Test
    public void findMatch_Success() throws Exception {
        List<String> rounds = new ArrayList<>();
        rounds.add("tu ket");

        Competition rawCompetition = mockRawCompetition("WC", rounds);
        competitionRepo.save(rawCompetition);

        Competitor rawCompetitor1 = mockRawCompetitor("11", rawCompetition);
        Competitor rawCompetitor2 = mockRawCompetitor("22", rawCompetition);
        competitorRepo.save(rawCompetitor1);
        competitorRepo.save(rawCompetitor2);

        Competitor competitor1 = competitorRepo.findOneByNameAndCompetitionName("11", "WC").orElse(null);
        Competitor competitor2 = competitorRepo.findOneByNameAndCompetitionName("22", "WC").orElse(null);
        Match rawMatch = mockRawCreateMatch(rawCompetition, "tu ket", competitor1, competitor2);
        matchRepo.save(rawMatch);

        Match match = matchService.findMatch(rawMatch.getId());
        if (Objects.isNull(match)) {
            assertThat(true).isEqualTo(false);
        }
        assertThat(match).isEqualToIgnoringNullFields(rawMatch);
    }

    @Test
    public void isUniqueMatchInRound_Success() throws Exception {
        boolean actual = matchService.isUniqueMatchInRound(1L, 2L, "asd");
        assertThat(actual).isEqualTo(true);
    }

    @Test
    public void isUniqueMatchInRound_Fail() throws Exception {
        List<String> rounds = new ArrayList<>();
        rounds.add("tu ket");

        Competition rawCompetition = mockRawCompetition("WC", rounds);
        competitionRepo.save(rawCompetition);

        Competitor rawCompetitor1 = mockRawCompetitor("11", rawCompetition);
        Competitor rawCompetitor2 = mockRawCompetitor("22", rawCompetition);
        competitorRepo.save(rawCompetitor1);
        competitorRepo.save(rawCompetitor2);

        Competitor competitor1 = competitorRepo.findOneByNameAndCompetitionName("11", "WC").orElse(null);
        Competitor competitor2 = competitorRepo.findOneByNameAndCompetitionName("22", "WC").orElse(null);
        Match rawMatch = mockRawCreateMatch(rawCompetition, "tu ket", competitor1, competitor2);
        matchRepo.save(rawMatch);

        boolean actual = matchService.isUniqueMatchInRound(competitor1.getId(), competitor2.getId(), "tu ket");
        assertThat(actual).isEqualTo(false);
    }

}
