package vn.kms.ngaythobet.domain.betting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BettingMatchRepository extends JpaRepository<BettingMatch, Long> {
    List<BettingMatch> findByBettingGroupIdAndMatchId(Long bettingGroupId,Long matchId);
    List<BettingMatch> findByBettingGroupId(Long bettingGroupId);
    Optional<BettingMatch> findOneById(Long id);
    List<BettingMatch> findByBettingGroupIdAndActive(Long bettingGroupId,boolean activate);
}
