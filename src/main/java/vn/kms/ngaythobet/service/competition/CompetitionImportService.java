/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.service.competition;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.domain.util.exception.InvalidCompetitionDataException;
import vn.kms.ngaythobet.service.file.Base64EncodeDecode;
import vn.kms.ngaythobet.service.file.FileService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class CompetitionImportService {
    public static final String ROUND_FORMAT = "%s.";

    @Value("${ngaythobet.competitor-logo}")
    private String competitorDeafultLogo;

    @Value("${app.upload-dir}")
    private String logoUploadDir;

    private final CompetitionRepository competitionRepo;
    private final CompetitorRepository competitorRepo;
    private final MatchRepository matchRepo;
    private final FileService fileService;

    public CompetitionImportService(CompetitionRepository competitionRepo, CompetitorRepository competitorRepo,
                                    MatchRepository matchRepo, FileService fileService) {
        this.competitionRepo = competitionRepo;
        this.competitorRepo = competitorRepo;
        this.matchRepo = matchRepo;
        this.fileService = fileService;
    }

    public void rollbackWhenInterrupted(String competitionName) {
        Optional<Competition> competition = competitionRepo.findOneByName(competitionName);

        if (!competition.isPresent()) {
            return;
        }
        matchRepo.removeByCompetitionName(competitionName);
        competitorRepo.removeByCompetitionName(competitionName);
        competitionRepo.removeByName(competitionName);
    }

    @Async
    public Future<Boolean> doImport(String competitionName, String aliasKey, String externalApiId) {
        try {
            CompetitionAPIData competitionAPIData = new CompetitionAPIData(competitionName, aliasKey, externalApiId);
            competitionAPIData.init();

            importCompetition(competitionAPIData);
            importCompetitors(competitionAPIData);
            importMatches(competitionAPIData);

            return new AsyncResult<>(true);
        } catch (InvalidCompetitionDataException e) {
            log.warn("Competition data is invalid: ", e);
            return new AsyncResult<>(false);
        }
    }

    private void importCompetition(CompetitionAPIData competitionAPIData) throws InvalidCompetitionDataException {
        preprocess(competitionAPIData.getCompetition());
        competitionAPIData.setCompetition(competitionRepo.save(competitionAPIData.getCompetition()));
    }

    private void importCompetitors(CompetitionAPIData competitionAPIData) {
        for (Competitor competitor : competitionAPIData.getCompetitors().getCompetitors()) {
            preprocess(competitor, competitionAPIData.getCompetition());
            if (!competitorRepo
                .findOneByNameAndCompetitionName(competitor.getName(), competitionAPIData.getCompetition().getName())
                .isPresent()) {
                competitorRepo.save(competitor);
            }
        }
    }

    private void importMatches(CompetitionAPIData competitionAPIData) {
        for (Match match : competitionAPIData.getMatches().getMatches()) {
            try {
                preprocess(match, competitionAPIData.getCompetition());
                if (!matchRepo.findOneMatch(match).isPresent()) {
                    matchRepo.save(match);
                }
            } catch (InvalidCompetitionDataException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private List<String> generateRounds(Competition competition) throws InvalidCompetitionDataException {
        return IntStream.range(0, competition.getNumberOfMatchdays())
            .mapToObj(index -> generateRound(index + 1))
            .collect(Collectors.toList());
    }

    private void preprocess(Competition competition) throws InvalidCompetitionDataException {
        competition.setStatus(Competition.Status.PUBLISHED);
        competition.setRounds(generateRounds(competition));
    }

    private void preprocess(Competitor competitor, Competition competition) {
        if (competitor.getCrestUrl() == null) {
            competitor.setLogo(competitorDeafultLogo);
        } else {
            competitor.setLogo(
                fileService.downloadLogoCompetitor(competitor.getCrestUrl(),
                    competitor.getCrestUrl().substring(competitor.getCrestUrl().lastIndexOf('/')+1),
                    Base64EncodeDecode.encode(competitor.getName() + "-" + competition.getId())));
        }

        if (competitor.getName() == null) {
            competitor.setName("");
        }

        competitor.setId(null);
        competitor.setCompetition(competition);
    }

    private void preprocess(Match match, Competition competition) throws InvalidCompetitionDataException {
        Optional<Competitor> competitor1;
        Optional<Competitor> competitor2;

        if (match.getHomeTeamName().equals(competition.getName())) {
            throw new InvalidCompetitionDataException("Match data are invalid: two competitors are the same.");
        }
        competitor1 = competitorRepo.findOneByNameAndCompetitionName(match.getHomeTeamName(), competition.getName());
        competitor2 = competitorRepo.findOneByNameAndCompetitionName(match.getAwayTeamName(), competition.getName());
        if (!competitor1.isPresent() || !competitor2.isPresent()) {
            throw new InvalidCompetitionDataException("Match data are invalid: not existing either of competitors.");
        }
        if (match.getStartTime() == null) {
            throw new InvalidCompetitionDataException("Match data are invalid: not existing start time.");
        }
        if (match.getLocation() == null) {
            match.setLocation("Unknown");
        }

        match.setId(null);
        match.setCompetition(competition);
        match.setCompetitor1(competitor1.get());
        match.setCompetitor2(competitor2.get());
        match.setRound(generateRound(match.getMatchday()));
        match.setScore1(match.getResult().getGoalsHomeTeam());
        match.setScore2(match.getResult().getGoalsAwayTeam());
    }

    private String generateRound(int matchday) {
        return String.format(ROUND_FORMAT, String.valueOf(matchday) + generteOrdinalSuffix(matchday));
    }

    private String generteOrdinalSuffix(int number) {
        int lastDigit = number % 10;
        int lastTwoDigit = number % 100;

        if (lastDigit == 1 && lastTwoDigit != 11) {
            return "st";
        }
        if (lastDigit == 2 && lastTwoDigit != 12) {
            return "nd";
        }
        if (lastDigit == 3 && lastTwoDigit != 13) {
            return "rd";
        }
        return "th";
    }

}
