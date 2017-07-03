/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.domain.util.exception.InvalidCompetitionDataException;
import vn.kms.ngaythobet.service.competition.CompetitionImportService;
import vn.kms.ngaythobet.service.competition.CompetitionService;
import vn.kms.ngaythobet.service.competition.CompetitorService;
import vn.kms.ngaythobet.service.competition.MatchService;
import vn.kms.ngaythobet.service.file.FileService;
import vn.kms.ngaythobet.web.form.*;
import vn.kms.ngaythobet.web.util.DataResponse;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static vn.kms.ngaythobet.web.form.CompetitionForm.copyDataFrom;

@Controller
@RequestMapping("/competitions")
@Slf4j
public class CompetitionController {
    private final int IMPORT_TIMEOUT = 60 * 5;

    @Value("${ngaythobet.football-token}")
    private String footballToken;

    private CompetitorService competitorService;
    private static final String COMPETITION = "competition-page";
    private static final String VIEW_COMPETITION = "competition/view";
    private CompetitionRepository competitionRepository;
    private MatchService matchService;
    private CompetitionService competitionService;
    private MessageSource messageSource;
    private CompetitionImportService competitionImportService;
    private FileService fileService;
    private AuthService authService;

    public CompetitionController(CompetitorService competitorService, CompetitionService competitionService, CompetitionRepository competitionRepository,
                                 MatchService matchService, CompetitionImportService competitionImportService,
                                 MessageSource messageSource, FileService fileService, AuthService authService) {
        this.competitorService = competitorService;
        this.competitionService = competitionService;
        this.competitionRepository = competitionRepository;
        this.matchService = matchService;
        this.messageSource = messageSource;
        this.competitionImportService = competitionImportService;
        this.fileService = fileService;
        this.authService=authService;
    }

    @GetMapping("/create-form")
    @ResponseBody
    public ModelAndView saveCompetition(CompetitionForm competitionForm) {
        ModelAndView modelAndView = new ModelAndView(COMPETITION);
        modelAndView.addObject("competitionForm", competitionForm);
        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    public DataResponse<List<ObjectError>> saveCompetitionSubmit(@Valid CompetitionForm competitionForm, BindingResult bindingResult) throws IOException {
        List<ObjectError> objectErrors = competitionService.filterError(competitionForm, bindingResult);
        if (!objectErrors.isEmpty()) {
            return new DataResponse<>(false, objectErrors);
        }

        Competition rawCompetition = new Competition();
        if (!competitionForm.getId().isEmpty()) {
            rawCompetition = competitionRepository.findOne(Long.parseLong(competitionForm.getId()));
        }

        if (!Objects.isNull(competitionForm.getLogoFile())) {
            String urlLogo = fileService.saveLogo(competitionForm.getLogoFile(),competitionForm.getAliasKey());
            rawCompetition.setLogo(urlLogo);
        } else if(rawCompetition.getId() !=  null) { //case update
            rawCompetition.setLogo(competitionRepository.findOne(rawCompetition.getId()).getLogo()); //set old logo for competition if admin don't choose any new logo.
        }

        rawCompetition.setAliasKey(competitionForm.getAliasKey());
        rawCompetition.setRounds(competitionForm.getRounds());
        rawCompetition.setName(competitionForm.getName());

        if("pressed".equals(competitionForm.getIsPressedPublish())) {
            rawCompetition.setStatus(Competition.Status.PUBLISHED);
        }

        if("PUBLISHED".equals(competitionForm.getStatus())) {
            rawCompetition.setStatus(Competition.Status.PUBLISHED);
        }

        competitionService.saveCompetition(rawCompetition);
        System.out.println(objectErrors.size());
        return new DataResponse<>(true);
    }

    @GetMapping("/{aliasKey}")
    public ModelAndView viewCompetition(@PathVariable String aliasKey, CompetitionForm competitionForm,
                                        GroupsInfo groupsInfo, CreateModeratorRequestForm createModeratorRequestForm) {

        Competition competition = competitionRepository.findOneByAliasKey(aliasKey).orElse(null);

        if (competition == null) {
            return null;
        }

        if (authService.getLoginUser().get().getRole().toString().equals("USER") && competition.getStatus().toString().equals("DRAFT")) {
            return new ModelAndView("redirect:/");
        }

        List<Match> matches = matchService.findByCompetitionId(competition.getId().toString());
        List<Competitor> competitors = competitorService.findAllByCompetition(aliasKey);

        competitionForm = copyDataFrom(competition);
        competitionForm.setNumberOfCompetitor(competitors.size());
        competitionForm.setNumberOfMatch(matches.size());

        groupsInfo.setTotalGroupsCount(competitionService.getGroupsCount(competition));
        groupsInfo.setPublishedGroupsCount(competitionService.getGroupsCount(competition, BettingGroup.Status.PUBLISHED));
        groupsInfo.setDraftGroupsCount(competitionService.getGroupsCount(competition, BettingGroup.Status.DRAFT));
        groupsInfo.setPendingGroupsCount(competitionService.getGroupsCount(competition, BettingGroup.Status.PENDING));

        ModelAndView modelAndView = new ModelAndView(VIEW_COMPETITION);
        modelAndView.addObject("matchRounds", filterByRound(competition, matches));
        modelAndView.addObject("competitionForm", competitionForm);
        modelAndView.addObject("groupsInfo", groupsInfo);
        modelAndView.addObject("competitors", competitors);

        modelAndView.addObject("competitionId", competition.getId());
        modelAndView.addObject("aliasKey", aliasKey);
        createModeratorRequestForm.setCompetitionId(competition.getId());

        return modelAndView;
    }

    @GetMapping("/{aliasKey}/update")
    public ModelAndView updateCompetition(@PathVariable String aliasKey, CompetitionForm competitionForm) {
        Competition competition = competitionRepository.findOneByAliasKey(aliasKey).orElse(null);
        List<Match> matches = matchService.findByCompetitionId(competition.getId().toString());
        List<Competitor> competitors = competitorService.findAllByCompetition(aliasKey);
        ModelAndView modelAndView = new ModelAndView(COMPETITION);

        if (competition == null) {
            return modelAndView;
        }
        competitionForm = copyDataFrom(competition);
        competitionForm.setNumberOfMatch(matches.size());
        competitionForm.setNumberOfCompetitor(competitors.size());
        modelAndView.addObject("competitionForm", competitionForm);
        return modelAndView;
    }

    @PostMapping("/{aliasKey}/publish")
    @ResponseBody
    public DataResponse<List<ObjectError>> publishCompetition(@PathVariable String aliasKey) {
        boolean isPublishSuccess = competitionService.publishCompetition(aliasKey);
        return new DataResponse<>(isPublishSuccess, Collections.emptyList());
    }

    private List<RoundForm> filterByRound(Competition competition, List<Match> matches) {
        List<RoundForm> roundFormList = new ArrayList<>();

        competition.getRounds().forEach(round -> {
            RoundForm roundForm = new RoundForm();
            roundForm.setName(round);
            List<Match> matchesList = matches.stream().filter(match -> match.getRound().equals(round)).collect(Collectors.toList());
            roundForm.setMatchList(matchesList);
            roundFormList.add(roundForm);
        });

        return roundFormList;
    }

    @GetMapping("/popup/auto-popup-import")
    public ModelAndView autoImportCompetition(ImportForm importForm) {
        ModelAndView modelAndView = new ModelAndView("auto-import-competition");
        modelAndView.addObject("importForm", importForm);
        modelAndView.addObject("footballToken", footballToken);
        return modelAndView;
    }

    @PostMapping("/auto-import")
    public String autoImportSubmit(@Valid @ModelAttribute("importForm") ImportForm importForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return importReponseAction(model, null, null);
        }
        try {
            Future<Boolean> importResult = competitionImportService.doImport(importForm.getCompetitionName(),
                importForm.getAliasKey(), importForm.getCompetitionId());

            if (importResult.get(IMPORT_TIMEOUT, TimeUnit.SECONDS)) {
                model.addAttribute("competitions", competitionRepository.findAllCompetitionWithOrder());
                return importReponseAction(model, false, 0);
            }

            competitionImportService.rollbackWhenInterrupted(importForm.getCompetitionName());
            return importReponseAction(model, true, 1);
        } catch (InterruptedException|ExecutionException e) {
            log.warn("Internal error while importing: ", e);

            competitionImportService.rollbackWhenInterrupted(importForm.getCompetitionName());
            return importReponseAction(model, true, 2);
        } catch (TimeoutException e) {
            log.warn("Import timeout: ", e);

            competitionImportService.rollbackWhenInterrupted(importForm.getCompetitionName());
            return importReponseAction(model, true, 3);
        }
    }

    private String importReponseAction(Model model, Boolean isFail, Integer messageKey) {
        model.addAttribute("importFail", isFail);
        model.addAttribute("messageKey", messageKey);
        return "auto-import-competition :: import-info";
    }
}
