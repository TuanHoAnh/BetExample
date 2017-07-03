/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.betting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.domain.user.User;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "betting_players")
@NoArgsConstructor
@EqualsAndHashCode(of = {"bettingMatch", "player"})
public class BettingPlayer extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="betting_match_id")
    private BettingMatch bettingMatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="player_id")
    private User player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="bet_competitor_id")
    private Competitor betCompetitor;

    public BettingPlayer(BettingMatch bettingMatch) {
        this.bettingMatch = bettingMatch;
    }

    public BigDecimal calculateLossAmount() {

        Match match = bettingMatch.getMatch();

        if (match.getScore1() == null || match.getScore2() == null) {
            return new BigDecimal(0.0);
        }

        if (betCompetitor == null) {
            return bettingMatch.getBettingAmount();
        }

        int score1 = match.getScore1();
        double balance1 = bettingMatch.getBalance1();

        int score2 = match.getScore2();
        double balance2 = bettingMatch.getBalance2();

        // calculate DIFF based on betCompetitor = match.competitor1
        double diff = (score1 + balance1) - (score2 + balance2);

        // reverse DIFF value if betCompetitor = match.competitor2
        if (betCompetitor.getId() == match.getCompetitor2().getId()) {
            diff = -diff;
        }

        if (diff >= 0.25) {
            return new BigDecimal(0.0);
        }

        if (diff <= -0.5) {
            return bettingMatch.getBettingAmount();
        }

        return bettingMatch.getBettingAmount().divide(new BigDecimal(2));
    }

    public String getFullName(){
        return player.getFirstName() + " " + player.getLastName();
    }
}
