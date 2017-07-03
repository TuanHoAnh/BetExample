package vn.kms.ngaythobet.web.interceptor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.user.UserRepository;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = Controller.class)
public class SideBarInterceptor {

    private final CompetitionRepository competitionRepo;
    private final UserRepository userRepo;

    public SideBarInterceptor(CompetitionRepository competitionRepo, UserRepository userRepo) {
        this.competitionRepo = competitionRepo;
        this.userRepo = userRepo;
    }

    @ModelAttribute(name = "competitions")
    public List<Competition> getAvailableCompetitions(Principal principal) {
        if (Objects.isNull(principal)) {
            return Collections.emptyList();
        }

        return competitionRepo.findAllCompetitionWithOrder();
    }

    @ModelAttribute(name = "user")
    public String getCurrentUser(Principal principal) {
        if (Objects.isNull(principal)) {
            return null;
        }

        return principal.getName();
    }

    @ModelAttribute(name = "competitionsJoining")
    public List<String> getCompetitionJoining(Principal principal) {
        if (Objects.isNull(principal)) {
            return Collections.emptyList();
        }

        return competitionRepo.findAllCompetitionWithOrder()
            .stream()
            .filter(competition ->
                competition.getGroups()
                    .stream()
                    .anyMatch(bettingGroup -> isDisplayedToPlayer(principal.getName(), bettingGroup)
                        || isDisplayedToModerator(principal.getName(), bettingGroup)))
            .map(Competition :: getAliasKey)
            .collect(Collectors.toList());

    }

    private boolean isDisplayedToModerator(String username, BettingGroup bettingGroup) {
        return bettingGroup.getModerator().getUsername().equals(username)
            && (bettingGroup.getStatus() == BettingGroup.Status.PUBLISHED || bettingGroup.getStatus() == BettingGroup.Status.DRAFT);
    }

    private boolean isDisplayedToPlayer(String username, BettingGroup bettingGroup) {
        return bettingGroup.getPlayers().contains(username)
            && bettingGroup.getStatus() == BettingGroup.Status.PUBLISHED;
    }
}
