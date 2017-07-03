/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;


import lombok.Data;
import vn.kms.ngaythobet.domain.betting.BettingMatch;
import vn.kms.ngaythobet.domain.betting.BettingPlayer;
import vn.kms.ngaythobet.domain.competition.Competitor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BetForm {

    private LocalDateTime timeLeft;

    private Competitor competitor1;

    private Competitor competitor2;

    private Integer score1;

    private Integer score2;

    private Double balance1;

    private Double balance2;

    private BigDecimal amount;

    private String comment;

    private List<BettingPlayer> players1;

    private List<BettingPlayer> players2;

    private Competitor selectedCompetitor;

    public static BetForm from(BettingMatch bettingMatch, Competitor selectedCompetitor){
        BetForm betForm = new BetForm();
        betForm.timeLeft = bettingMatch.getExpiryTime();
        betForm.competitor1 = bettingMatch.getMatch().getCompetitor1();
        betForm.competitor2 = bettingMatch.getMatch().getCompetitor2();
        betForm.score1 = bettingMatch.getMatch().getScore1();
        betForm.score2 = bettingMatch.getMatch().getScore2();
        betForm.balance1 = bettingMatch.getBalance1();
        betForm.balance2 = bettingMatch.getBalance2();
        betForm.amount = bettingMatch.getBettingAmount();
        betForm.comment = bettingMatch.getComment();
        betForm.players1 = new ArrayList<BettingPlayer>(bettingMatch.getBettingPlayersByCompetitor(betForm.competitor1));
        betForm.players2 = new ArrayList<BettingPlayer>(bettingMatch.getBettingPlayersByCompetitor(betForm.competitor2));
        betForm.selectedCompetitor= selectedCompetitor;
        return betForm;
    }

}
