package vn.kms.ngaythobet.domain.betting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.domain.user.User;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class BettingPlayerTest {
    private BettingPlayer bettingPlayer;

    @Before
    public void startUp() {
        Competitor competitor1 = new Competitor("Argentina");
        competitor1.setId(1L);

        Competitor competitor2 = new Competitor("Brazil");
        competitor2.setId(2L);

        Match match = new Match(competitor1, competitor2);

        BettingMatch bettingMatch = new BettingMatch(match);
        bettingMatch.setBettingAmount(new BigDecimal(10.0));

        bettingPlayer = new BettingPlayer(bettingMatch);
    }

    @Test
    public void testCalculateLossAmountNonLoss1() {
        setScoreAndBalance(1, 0, 0, 0.5);

        // player chose Argentina
        bettingPlayer.setBetCompetitor(bettingPlayer.getBettingMatch().getMatch().getCompetitor1());
        BigDecimal lossAmount = bettingPlayer.calculateLossAmount();

        // player should be non-loss
        assertThat(lossAmount).isEqualTo(new BigDecimal(0.0));
    }

    @Test
    public void testCalculateLossAmountNonLoss2() {
        setScoreAndBalance(0, 0, 0, 1);

        // player chose Brazil
        bettingPlayer.setBetCompetitor(bettingPlayer.getBettingMatch().getMatch().getCompetitor2());
        BigDecimal lossAmount = bettingPlayer.calculateLossAmount();

        // player should be non-loss
        assertThat(lossAmount).isEqualTo(new BigDecimal(0.0));
    }

    @Test
    public void testCalculateLossAmountHalfLoss() {
        setScoreAndBalance(0, 0, 0, 0);

        // player chose Brazil
        bettingPlayer.setBetCompetitor(bettingPlayer.getBettingMatch().getMatch().getCompetitor2());
        BigDecimal lossAmount = bettingPlayer.calculateLossAmount();

        // player should be half-loss
        assertThat(lossAmount).isEqualTo(new BigDecimal(5.0));
    }

    @Test
    public void testCalculateLossAmountFullLoss() {
        setScoreAndBalance(0, 0, 0, 0.5);

        // player chose Argentina
        bettingPlayer.setBetCompetitor(bettingPlayer.getBettingMatch().getMatch().getCompetitor1());
        BigDecimal lossAmount = bettingPlayer.calculateLossAmount();

        // player should be full-loss
        assertThat(lossAmount).isEqualTo(new BigDecimal(10.0));
    }

    private void setScoreAndBalance(int score1, double balance1, int score2, double balance2) {
        BettingMatch bettingMatch = bettingPlayer.getBettingMatch();
        Match match = bettingMatch.getMatch();

        // [Argentina] score1   : score2   [Brazil]
        match.setScore1(score1);
        match.setScore2(score2);

        // [Argentina] balance1 : balance2 [Brazil]
        bettingMatch.setBalance1(balance1);
        bettingMatch.setBalance2(balance2);
    }

    @Test
    public void testGetFullName(){
        User user = new User();
        user.setFirstName("Hung");
        user.setLastName("Nguyen");
        bettingPlayer.setPlayer(user);
        assertThat(bettingPlayer.getFullName()).isEqualTo("Hung Nguyen");
    }
}
