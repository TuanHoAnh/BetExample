package vn.kms.ngaythobet.service.competition;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.domain.competition.MatchRepository;
import vn.kms.ngaythobet.web.form.CreateMatchForm;
import vn.kms.ngaythobet.web.util.DataResponse;
import vn.kms.ngaythobet.web.util.DateTimeFormat;

import java.util.Collections;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepo;
    private final CompetitionService competitionService;

    public MatchService(MatchRepository matchRepo, CompetitionService competitionService) {
        this.matchRepo = matchRepo;
        this.competitionService = competitionService;
    }

    public boolean updateMatchScore(Long id, Match rawMatch) {
        Match match = matchRepo.findOneById(id);
        if (!Objects.isNull(match) && match.isValidToUpdateMatchScore()) {
            match.setScore1(rawMatch.getScore1());
            match.setScore2(rawMatch.getScore2());
            return Optional.ofNullable(matchRepo.save(match)).isPresent();
        }
        return false;
    }

    public List<Match> findByCompetitionId(String competitionId) {
        return matchRepo.findByCompetitionId(Long.parseLong(competitionId));
    }

    public List<Match> findByAliasKey(String aliasKey) {
        Competition competition = competitionService.findOneByAliasKey(aliasKey);
        if (competition == null) {
            return Collections.emptyList();
        }
        return matchRepo.findByCompetitionId(competition.getId());
    }

    public void saveMatch(Match match){
        matchRepo.save(match);
    }

    public Match findMatch(Long id){
        return matchRepo.findOneById(id);
    }

    public boolean isUniqueMatchInRound(Long competitor1, Long competitor2, String round){
        return matchRepo.findByCompetitor1IdAndCompetitor2IdAndRound(competitor1,competitor2,round) == null;

    }

    public boolean isUniqueMatchInRoundUpdate(Long competitor1, Long competitor2, String round, Long matchId){
        Match lastestMatch = findMatch(matchId);

        return (lastestMatch.getCompetitor1().getId().equals(competitor1)) &&
            (lastestMatch.getCompetitor2().getId().equals(competitor2)) &&
            (lastestMatch.getRound().equals(round));
    }

    public boolean isValidToCreateScore(String startDate, String startTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat.DATETIME.getFormat());
        LocalDateTime startDateTime = LocalDateTime.parse(startDate + " " + startTime, formatter);
        return startDateTime.isBefore(LocalDateTime.now());
    }

    public CreateMatchForm setMatchFormById(Long id){
        Match match = findMatch(id);
        CreateMatchForm createMatchForm = new CreateMatchForm();
        BeanUtils.copyProperties(match,createMatchForm);
        createMatchForm.setCompetitionId(match.getCompetition().getId());
        createMatchForm.setCompetitor1Id(match.getCompetitor1().getId());
        createMatchForm.setCompetitor2Id(match.getCompetitor2().getId());

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(DateTimeFormat.DATE.getFormat());
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(DateTimeFormat.TIME.getFormat());

        String formatDate = match.getStartTime().format(formatterDate);
        String formatTime = match.getStartTime().format(formatterTime);

        createMatchForm.setMatchDate(formatDate);
        createMatchForm.setMatchTime(formatTime);
        return createMatchForm;
    }
}
