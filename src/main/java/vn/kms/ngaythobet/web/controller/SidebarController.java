package vn.kms.ngaythobet.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/sidebar")
public class SidebarController {
    private final String PATTERN_COMPETITION = "competitions/([A-Za-z0-9]+)";
    private final String PATTERN_GROUP = "bettings/([0-9]+)";

    private CompetitionRepository competitionRepository;
    private BettingGroupRepository bettingGroupRepository;

    public SidebarController(CompetitionRepository competitionRepository, BettingGroupRepository bettingGroupRepository) {
        this.competitionRepository = competitionRepository;
        this.bettingGroupRepository = bettingGroupRepository;
    }

    @GetMapping
    public String reloadSidebar(Model model, @RequestParam String hashUrl) {
        Pattern competitionPattern = Pattern.compile(PATTERN_COMPETITION);
        Pattern groupPattern = Pattern.compile(PATTERN_GROUP);
        Matcher competitionMatcher = competitionPattern.matcher(hashUrl);
        Matcher groupMatcher = groupPattern.matcher(hashUrl);
        List<Competition> competitions = competitionRepository.findAllCompetitionWithOrder();
        String activeCompetitionAliaskey = "";
        Long bettingGroupId = null;

        if (groupMatcher.find()
            && bettingGroupRepository.findOne(Long.parseLong(groupMatcher.group(1))) != null) {
            bettingGroupId = Long.parseLong(groupMatcher.group(1));
        }

        if (competitionMatcher.find()
            && competitions.stream().anyMatch(competition -> competition.getAliasKey().equals(competitionMatcher.group(1)))) {
            activeCompetitionAliaskey = competitionMatcher.group(1);
        }

        model.addAttribute("activeCompetition", activeCompetitionAliaskey);
        model.addAttribute("activeGroup", bettingGroupId);
        model.addAttribute("competitions", competitions);
        return "fragments/sidebar :: sidebar-competitions";
    }
}
