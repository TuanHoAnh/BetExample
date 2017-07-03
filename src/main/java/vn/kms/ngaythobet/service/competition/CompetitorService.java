package vn.kms.ngaythobet.service.competition;


import org.springframework.stereotype.Service;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.CompetitorRepository;
import vn.kms.ngaythobet.service.competition.CompetitionService;

import java.util.Collections;
import java.util.List;

@Service
public class CompetitorService {
    private final CompetitorRepository competitorRepository;
    private final CompetitionService competitionService;

    public CompetitorService(CompetitorRepository competitorRepo, CompetitionService competitionService) {
        this.competitorRepository = competitorRepo;
        this.competitionService = competitionService;
    }

    public List<Competitor> findAllByCompetition(String aliasKey){
        Competition competition = competitionService.findOneByAliasKey(aliasKey);
        if (competition == null) {
            return Collections.emptyList();
        }
        return competitorRepository.findAllByCompetition(competitionService.findOneByAliasKey(aliasKey));
    }

    public Competitor findOneById(Long id){
        return competitorRepository.findById(id).orElse(null);
    }

    public void saveCompetitor(Competitor rawCompetitor) {
        competitorRepository.save(rawCompetitor);
    }
}
