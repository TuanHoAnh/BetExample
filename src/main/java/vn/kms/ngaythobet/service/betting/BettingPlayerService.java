/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.service.betting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.kms.ngaythobet.domain.betting.BettingMatch;
import vn.kms.ngaythobet.domain.betting.BettingMatchRepository;
import vn.kms.ngaythobet.domain.betting.BettingPlayer;
import vn.kms.ngaythobet.domain.betting.BettingPlayerRepository;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.CompetitorRepository;
import vn.kms.ngaythobet.domain.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BettingPlayerService {

    private CompetitorRepository competitorRepository;

    private BettingPlayerRepository bettingPlayerRepository;

    private BettingMatchRepository bettingMatchRepository;
    @Autowired
    public BettingPlayerService(CompetitorRepository competitorRepository, BettingPlayerRepository bettingPlayerRepository, BettingMatchRepository bettingMatchRepository){
        this.competitorRepository = competitorRepository;
        this.bettingPlayerRepository = bettingPlayerRepository;
        this.bettingMatchRepository = bettingMatchRepository;
    }

    public BettingPlayer addBettingPlayer(Long bettingMatchId, Long betCompetitorId, User player){
        Competitor betCompetitor = competitorRepository.findOne(betCompetitorId);
        BettingMatch bettingMatch = bettingMatchRepository.findOne(bettingMatchId);
        Optional<BettingPlayer> oldBettingPlayer = bettingPlayerRepository.findOneByPlayerAndBettingMatch(player, bettingMatch);
        if(oldBettingPlayer.isPresent()){
            oldBettingPlayer.get().setBetCompetitor(betCompetitor);
            return bettingPlayerRepository.save(oldBettingPlayer.get());
        }
        BettingPlayer bettingPlayer = new BettingPlayer();
        bettingPlayer.setBetCompetitor(betCompetitor);
        bettingPlayer.setBettingMatch(bettingMatch);
        bettingPlayer.setModifiedAt(LocalDateTime.now());
        bettingPlayer.setCreatedBy(bettingMatch.getBettingGroup().getModerator().getFullName());
        bettingPlayer.setPlayer(player);
        return bettingPlayerRepository.save(bettingPlayer);
    }

    public Competitor getSelectedCompetitor(User user, BettingMatch bettingMatch){
        Optional<BettingPlayer> player = bettingPlayerRepository.findOneByPlayerAndBettingMatch(user, bettingMatch);
        if(player.isPresent()) {
            return player.get().getBetCompetitor();
        }
        return new Competitor();
    }
}
