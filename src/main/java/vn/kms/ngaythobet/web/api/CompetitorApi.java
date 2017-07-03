package vn.kms.ngaythobet.web.api;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.CompetitorRepository;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.service.competition.CompetitorService;
import vn.kms.ngaythobet.web.form.CompetitorForm;
import vn.kms.ngaythobet.web.form.UpdateMatchScoreForm;
import vn.kms.ngaythobet.web.util.DataResponse;

/**
 * Created by phuvha on 4/21/2017.
 */
@RestController
@RequestMapping("/api/competitor")
public class CompetitorApi {

    private CompetitorRepository competitorRepository;

    private CompetitorService competitorService;

    public CompetitorApi(CompetitorRepository competitorRepository, CompetitorService competitorService) {
        this.competitorRepository = competitorRepository;
        this.competitorService = competitorService;
    }

    @GetMapping("/{competitorId}")
    public DataResponse<CompetitorForm> getMatch(@PathVariable(name = "competitorId") Long competitorId) {
        Competitor competitor = competitorRepository.findOne(competitorId);
        if (competitor == null) {
            return new DataResponse<>(false);
        }
        CompetitorForm competitorForm = new CompetitorForm();
        BeanUtils.copyProperties(competitor, competitorForm);
        return new DataResponse<>(competitorForm);
    }
}
