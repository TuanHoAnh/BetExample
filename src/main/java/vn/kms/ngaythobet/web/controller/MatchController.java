package vn.kms.ngaythobet.web.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.service.competition.CompetitionService;
import vn.kms.ngaythobet.service.competition.CompetitorService;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.service.competition.MatchService;
import vn.kms.ngaythobet.web.form.CreateMatchForm;
import vn.kms.ngaythobet.web.util.DataResponse;
import vn.kms.ngaythobet.web.util.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping("/competitions/{aliasKey}")
public class MatchController {
    @Autowired
    private MatchService matchService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitorService competitorService;

    @GetMapping("/createMatchPopup")
    public ModelAndView createMatchPopup(CreateMatchForm createMatchForm, @RequestParam String aliasKey, @RequestParam Long id) {
        ModelAndView modelAndView = new ModelAndView("creatematch-popup");
        if (id != null){
            createMatchForm = matchService.setMatchFormById(id);
        }
        Competition competition = competitionRepository.findOneByAliasKey(aliasKey).orElse(null);
        modelAndView.addObject("competitionId",competition.getId());
        modelAndView.addObject("createMatchForm", createMatchForm);
        modelAndView.addObject("listCompetitors", competitorService.findAllByCompetition(aliasKey));
        modelAndView.addObject("listRounds", competition.getRounds());
        return modelAndView;
    }


    @PostMapping("/createMatch")
    public String createMatchSubmit(@Valid @ModelAttribute("createMatchForm") CreateMatchForm createMatchForm,
                                    BindingResult bindingResult,Model model, @PathVariable("aliasKey") String aliasKey) {

        Competition competition = competitionRepository.findOneByAliasKey(aliasKey).orElse(null);
        createMatchForm.setCompetitionId(competition.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("createMatchForm",createMatchForm);
            model.addAttribute("listCompetitors",competitorService.findAllByCompetition(aliasKey));
            model.addAttribute("listRounds", competition.getRounds());
            return "creatematch-popup :: creatematch-info";
        }
        createMatchForm.setCompetitor1(competitorService.findOneById(createMatchForm.getCompetitor1Id()));
        createMatchForm.setCompetitor2(competitorService.findOneById(createMatchForm.getCompetitor2Id()));
        createMatchForm.setCompetition(competitionService.findOneById(createMatchForm.getCompetitionId()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat.DATETIME.getFormat());
        LocalDateTime dateTime = LocalDateTime.parse(createMatchForm.getMatchDate()+" "+createMatchForm.getMatchTime(), formatter);
        createMatchForm.setStartTime(dateTime);
        Match match=new Match();
        BeanUtils.copyProperties(createMatchForm,match);
        matchService.saveMatch(match);
        return "creatematch-popup";
    }
}
