/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.betting;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface BettingPlayerRepository extends JpaRepository<BettingPlayer, Long> {
    Optional<BettingPlayer> findOneByPlayerAndBettingMatch(User player, BettingMatch bettingMatch);
    List<BettingPlayer> findBettingPlayerByBetCompetitor(Competitor competitor);
}
