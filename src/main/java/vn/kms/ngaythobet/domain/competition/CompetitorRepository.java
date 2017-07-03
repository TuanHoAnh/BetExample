/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.competition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CompetitorRepository extends JpaRepository<Competitor, Long> {
    List<Competitor> findAllByCompetition(Competition competition);
    Optional<Competitor> findById(Long id);
    Optional<Competitor> findOneByNameAndCompetitionName(String name, String competitionName);
    Optional<Competitor> findOneById(Long id);

    @Transactional
    List<Competitor> removeByCompetitionName(String competitionName);
}
