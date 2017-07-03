package vn.kms.ngaythobet.web.api;

import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.domain.competition.MatchRepository;
import vn.kms.ngaythobet.service.competition.MatchService;
import vn.kms.ngaythobet.web.form.UpdateMatchScoreForm;
import vn.kms.ngaythobet.web.util.DataResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/match")
public class MatchApi {
    private MatchRepository matchRepository;

    private MatchService matchService;

    private AuthService authService;

    public MatchApi(MatchRepository matchRepository, MatchService matchService, AuthService authService) {
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.authService = authService;
    }

    @GetMapping("/{matchId}")
    public DataResponse<UpdateMatchScoreForm> getMatch(@PathVariable(name = "matchId") Long matchId) {
        Match match = matchRepository.findOne(matchId);

        if (match == null) {
            return new DataResponse<>(false);
        }

        UpdateMatchScoreForm updateMatchScoreForm = new UpdateMatchScoreForm();
        BeanUtils.copyProperties(match, updateMatchScoreForm);
        return new DataResponse<>(updateMatchScoreForm);
    }

    @PutMapping("/update-match-score/{matchId}")
    public DataResponse updateMatchScore(
        @PathVariable(name = "matchId") Long matchId,
        @Valid @RequestBody UpdateMatchScoreForm updateMaxScoreForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new DataResponse<>(false, bindingResult.getAllErrors());
        }

        Match rawMatch = new Match();
        BeanUtils.copyProperties(updateMaxScoreForm, rawMatch);

        if (!matchService.updateMatchScore(matchId, rawMatch)) {
            return new DataResponse<>(false);
        }

        return new DataResponse<>(true);
    }
}
