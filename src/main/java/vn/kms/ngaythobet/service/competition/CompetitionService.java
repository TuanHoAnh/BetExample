package vn.kms.ngaythobet.service.competition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.web.form.CompetitionForm;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompetitionService {

    private static final int ERROR_NAME_ROW = 3;
    private static final int MIN_NUMBER_OF_MATCH = 1;
    private static final int MIN_NUMBER_OF_COMPETITOR = 2;
    private CompetitionRepository competitionRepo;
    private CompetitorRepository competitorRepository;
    private MatchRepository matchRepository;
    private BettingGroupRepository bettingGroupRepository;

    @Autowired
    public CompetitionService(CompetitionRepository competitionRepo, CompetitorRepository competitorRepository, MatchRepository matchRepository, BettingGroupRepository bettingGroupRepository) {
        this.competitionRepo = competitionRepo;
        this.competitorRepository = competitorRepository;
        this.matchRepository = matchRepository;
        this.bettingGroupRepository = bettingGroupRepository;
    }

    public Competition findOneByName(String name) {
        return competitionRepo.findOneByName(name).orElse(null);
    }

    public void saveCompetition(Competition rawCompetition){
        competitionRepo.save(rawCompetition);
    }

    public Competition findOneById(String id) {
        return competitionRepo.findOne(Long.parseLong(id));
    }

    public boolean publishCompetition(String aliasKey) {
        Competition competition = competitionRepo.findOneByAliasKey(aliasKey).orElse(null);
        if(competition == null) {
            return false;
        }
        List<Match> matches = matchRepository.findByCompetitionId(competition.getId());
        List<Competitor> competitors = competitorRepository.findAllByCompetition(competition);
        if(matches.size() >= MIN_NUMBER_OF_MATCH && competitors.size() >= MIN_NUMBER_OF_COMPETITOR) {
            competition.setStatus(Competition.Status.PUBLISHED);
            competitionRepo.save(competition);
            return true;
        }
        return false;
    }

    private List<ObjectError> filterErrorWhenCreateCompetition(CompetitionForm competitionForm, List<FieldError> fieldErrors) {
        List<ObjectError> newFieldErrors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String error = fieldError.getCodes()[ERROR_NAME_ROW];
            Object rejectedValue = fieldError.getRejectedValue();
            if(!"pressed".equals(competitionForm.getIsPressedPublish()) && "GreaterThan".equals(error)) {
                continue;
            }
            if("undefined".equals(rejectedValue)) {
                continue;
            }
            newFieldErrors.add(fieldError);
        }
        return newFieldErrors;
    }

    private List<ObjectError> filterErrorWhenUpdateCompetition(CompetitionForm competitionForm, List<FieldError> fieldErrors) {
        List<ObjectError> newFieldErrors = new ArrayList<>();
        Competition oldCompetition = competitionRepo.findOne(Long.parseLong(competitionForm.getId()));
        for (FieldError fieldError : fieldErrors) {
            String fieldName = fieldError.getField();
            String error = fieldError.getCodes()[ERROR_NAME_ROW];
            Object rejectedValue = fieldError.getRejectedValue();
            if (("aliasKey".equals(fieldName) || "name".equals(fieldName)) && "FieldUnique".equals(error)
                && rejectedValue.equals(oldCompetition.getValueByFieldName(fieldName))) {
                //Field unique is the same as itself in database.
                continue;
            }
            if(!"pressed".equals(competitionForm.getIsPressedPublish()) && "GreaterThan".equals(error)) {
                continue;
            }
            if("undefined".equals(rejectedValue)) {
                continue;
            }
            newFieldErrors.add(fieldError);
        }
        return newFieldErrors;
    }

    public List<ObjectError> filterError(CompetitionForm competitionForm, BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (competitionForm.getId().isEmpty()) { //Case create new competition, we need to filter error of publish action.
            return filterErrorWhenCreateCompetition(competitionForm,fieldErrors);
        }
        //Case update competition, we need to filter both publish action errors and unique field errors
        return filterErrorWhenUpdateCompetition(competitionForm,fieldErrors);
    }

    public Competition findOneById(Long id){
        return competitionRepo.findById(id).orElse(null);
    }

    public Competition findOneByAliasKey(String key){
        return competitionRepo.findOneByAliasKey(key).orElse(null);
    }

    public Integer getGroupsCount(Competition competition, BettingGroup.Status status) {
        return bettingGroupRepository.countByCompetitionAndStatus(competition, status);
    }

    public Integer getGroupsCount(Competition competition) {
        return bettingGroupRepository.countByCompetition(competition);
    }
}
