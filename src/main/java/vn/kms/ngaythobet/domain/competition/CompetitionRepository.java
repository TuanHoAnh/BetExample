package vn.kms.ngaythobet.domain.competition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @Query("SELECT competition FROM Competition competition ORDER BY CASE WHEN competition.createdAt IS NULL THEN 1 ELSE 0 END, competition.createdAt DESC")
    List<Competition> findAllCompetitionWithOrder();

    Optional<Competition> findOneByName(String name);
    Optional<Competition> findOneByAliasKey(String aliasKey);
    Optional<Competition> findById(Long id);

    @Transactional
    List<Competition> removeByName(String name);
}
