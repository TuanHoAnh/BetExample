package vn.kms.ngaythobet.domain.util;

import vn.kms.ngaythobet.domain.betting.BettingMatch;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.web.form.BettingMatchRoundForm;
import vn.kms.ngaythobet.web.form.RoundForm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoundFormFilter {
    public static List<BettingMatchRoundForm> filterByRound(Competition competition, List<BettingMatch> matches) {
        List<BettingMatchRoundForm> roundFormList = new ArrayList<>();

        competition.getRounds().forEach(round -> {
            BettingMatchRoundForm roundForm = new BettingMatchRoundForm();
            roundForm.setName(round);
            List<BettingMatch> matchesList = matches.stream().filter(match -> match.getMatch().getRound().equals(round)).collect(Collectors.toList());
            roundForm.setMatchList(matchesList);
            roundFormList.add(roundForm);
        });

        return roundFormList;
    }
}
