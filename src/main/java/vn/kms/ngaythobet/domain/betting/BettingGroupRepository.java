/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.betting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.kms.ngaythobet.domain.competition.Competition;


import java.util.List;
import java.util.Optional;

public interface BettingGroupRepository extends JpaRepository<BettingGroup, Long>, BettingGroupRepositoryCustom {

    List<BettingGroup> findAllByStatus(BettingGroup.Status status);

    Integer countBettingGroupsByStatus(BettingGroup.Status status);

    void deleteBettingGroupById(Long id);

    Optional<BettingGroup> findOneByName(String name);

    Optional<BettingGroup> findOneById(Long id);

    Optional<BettingGroup> findBettingGroupById(Long id);

    Page<BettingGroup> findAllByStatus(BettingGroup.Status status, Pageable pageable);

    @Modifying
    @Query("update BettingGroup b set b.status=:status where b.id=:id")
    void updateStatusBettingGroup(@Param("status") BettingGroup.Status status, @Param("id") Long id);

    Integer countByCompetitionAndStatus(Competition competition, BettingGroup.Status status);

    Integer countByCompetition(Competition competition);
}
