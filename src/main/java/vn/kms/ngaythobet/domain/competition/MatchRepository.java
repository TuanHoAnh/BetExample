package vn.kms.ngaythobet.domain.competition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.competition.id=:competitionId ORDER BY m.startTime asc")
    List<Match> findByCompetitionId(@Param("competitionId") Long competitionId);

    Match findOneById(Long id);

    Match findByCompetitor1IdAndCompetitor2IdAndRound(Long competitor1_id, Long competitor2_id, String round);

    @Transactional
    List<Match> removeByCompetitionName(String competitionName);

    Optional<Match> findOneByCompetitionNameAndCompetitor1NameAndCompetitor2NameAndRound(String competitionName,
                                                                                         String competitor1Name,
                                                                                         String competitor2Name,
                                                                                         String round);
    default Optional<Match> findOneMatch(Match match) {
        Optional<Match> result = findOneByCompetitionNameAndCompetitor1NameAndCompetitor2NameAndRound(
            match.getCompetition().getName(),
            match.getAwayTeamName(),
            match.getHomeTeamName(),
            match.getRound());

        if (result.isPresent()) {
            return result;
        }

        return findOneByCompetitionNameAndCompetitor1NameAndCompetitor2NameAndRound(
            match.getCompetition().getName(),
            match.getHomeTeamName(),
            match.getAwayTeamName(),
            match.getRound());
    }

    List<Match> findByCompetitionAndRound(Competition competition,String round);
}
