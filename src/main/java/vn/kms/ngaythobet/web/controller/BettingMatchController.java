package vn.kms.ngaythobet.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.kms.ngaythobet.domain.betting.*;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.service.betting.BettingMatchService;
import vn.kms.ngaythobet.service.betting.BettingPlayerService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.form.BetForm;
import vn.kms.ngaythobet.web.form.BettingMatchForm;
import vn.kms.ngaythobet.web.util.DateTimeFormat;
import vn.kms.ngaythobet.web.util.EmailValidate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by congle on 4/10/2017.
 */
@Controller
@Transactional
@RequestMapping("/competitions/{aliasKey}/bettings")
public class BettingMatchController {

    private static final String BETTING_MATCH_DETAIL_FOR_BET_FORM = "content/betting-match-detail-for-bet-form :: betting-match-detail-for-bet";

    private AuthService authService;

    private MatchRepository matchRepository;

    private BettingMatchService bettingMatchService;

    private BettingMatchRepository bettingMatchRepository;

    private BettingGroupRepository bettingGroupRepository;

    private UserService userService;

    private CompetitionRepository competitionRepository;

    private MailingService mailingService;

    private CompetitorRepository competitorRepository;

    private BettingPlayerService bettingPlayerService;


    @Autowired
    public BettingMatchController(MatchRepository matchRepository, BettingMatchService bettingMatchService,
                                  BettingMatchRepository bettingMatchRepository, BettingGroupRepository bettingGroupRepository,
                                  AuthService authService, UserService userService, CompetitionRepository competitionRepository,
                                  MailingService mailingService, CompetitorRepository competitorRepository, BettingPlayerService bettingPlayerService) {
        this.matchRepository = matchRepository;
        this.bettingMatchService = bettingMatchService;
        this.bettingMatchRepository = bettingMatchRepository;
        this.bettingGroupRepository = bettingGroupRepository;
        this.authService = authService;
        this.userService = userService;
        this.competitionRepository = competitionRepository;
        this.mailingService = mailingService;
        this.competitorRepository = competitorRepository;
        this.bettingPlayerService = bettingPlayerService;
    }


    @GetMapping("/{bettingGroupId}/matches")
    public String bettingMatchHome(@PathVariable String aliasKey, @PathVariable Long bettingGroupId, BettingMatchForm bettingMatchForm, HttpServletRequest httpServletRequest) {
        Optional<BettingGroup> bettingGroup = bettingGroupRepository.findOneById(bettingGroupId);
        if (bettingGroup.isPresent()) {
            Long moderatorId = bettingGroup.get().getModerator().getId();

            List<User> players = userService.convertListUsernameToListUser(bettingGroup.get().getPlayers());
            authService.getLoginUser().ifPresent(user -> {
                Long currentUserId = user.getId();
                if (moderatorId.equals(currentUserId)) {
                    httpServletRequest.setAttribute("moderatorId", currentUserId);
                    httpServletRequest.setAttribute("listBettingMatch", bettingMatchRepository.findByBettingGroupId(bettingGroupId));
                } else {
                    httpServletRequest.setAttribute("players", players);
                    httpServletRequest.setAttribute("listBettingMatchActive", bettingMatchRepository.findByBettingGroupIdAndActive(bettingGroupId, true));
                }
            });
        }

        Optional<Competition> competition = competitionRepository.findOneByAliasKey(aliasKey);
        List<String> listRound = competition.get().getRounds();


        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bettingMatchForm.setDate(LocalDateTime.now().format(formatterDate));

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        bettingMatchForm.setTime(LocalDateTime.now().format(formatterTime));

        httpServletRequest.setAttribute("listBettingMatch", bettingMatchRepository.findByBettingGroupId(bettingGroupId));
        httpServletRequest.setAttribute("bettingGroupId", bettingGroupId);
        httpServletRequest.setAttribute("aliasKey", aliasKey);
        httpServletRequest.setAttribute("listRound", listRound);

        return "bettingmatch/betting-match";

    }

    @GetMapping("{groupId}/matches-by-round")
    @ResponseBody
    public List<Match> getMatchName(@PathVariable String aliasKey, @RequestParam String round) {
        Optional<Competition> competition = competitionRepository.findOneByAliasKey(aliasKey);
        return  matchRepository.findByCompetitionAndRound(competition.get(), round);
    }

    @GetMapping("/{bettingGroupId}/matches/{bettingMatchId}")
    @Transactional
    public ModelAndView updateBettingMathPage(@PathVariable Long bettingGroupId, @PathVariable String aliasKey, @PathVariable Long bettingMatchId, BettingMatchForm bettingMatchForm) {

        ModelAndView modelAndView = new ModelAndView("bettingmatch/betting-match");
        BettingMatch bettingMatch = bettingMatchRepository.findOne(bettingMatchId);
        if (bettingMatch == null) {
            return modelAndView;
        }

        Optional<BettingGroup> bettingGroup = bettingGroupRepository.findOneById(bettingGroupId);
        Long moderatorId = bettingGroup.get().getModerator().getId();
        authService.getLoginUser().ifPresent(user -> {
            Long currentUserId = user.getId();
            if (moderatorId == currentUserId) {
                modelAndView.addObject("moderatorId", currentUserId);
            }
        });

        bettingMatchForm = BettingMatchForm.from(bettingMatch);

        if (bettingMatch.isActive()) {
            modelAndView.addObject("publishButton", true);
        }

        Optional<Competition> competition = competitionRepository.findOneByAliasKey(aliasKey);
        List<String> listRound = competition.get().getRounds();
        modelAndView.addObject("listRound", listRound);
        modelAndView.addObject("bettingMatchForm", bettingMatchForm);
        modelAndView.addObject("aliasKey", aliasKey);
        modelAndView.addObject("bettingMatchId", bettingMatchId);
        modelAndView.addObject("competitor1", matchRepository.getOne(bettingMatch.getMatch().getId()).getCompetitor1().getName());
        modelAndView.addObject("competitor2", matchRepository.getOne(bettingMatch.getMatch().getId()).getCompetitor2().getName());


        return modelAndView;
    }

    @PostMapping("/{bettingGroupId}/matches")
    public String saveBettingMatch(@PathVariable String aliasKey, @PathVariable Long bettingGroupId, @Valid @ModelAttribute("bettingMatchForm") BettingMatchForm bettingMatchForm,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            Optional<Competition> competition = competitionRepository.findOneByAliasKey(aliasKey);
            List<String> listRound = competition.get().getRounds();
            List<Match> listBettingMatches =  matchRepository.findByCompetitionAndRound(competition.get(), bettingMatchForm.getRound());
            model.addAttribute("listBettingMatches",listBettingMatches);
            model.addAttribute("listRound", listRound);
            return "bettingmatch/betting-match :: info-content";
        }
        BettingMatch bettingMatch = new BettingMatch();
        if (!bettingMatchForm.getId().equals("")) {
            bettingMatch = bettingMatchRepository.findOne(Long.valueOf(bettingMatchForm.getId()));
            if (bettingMatch.isActive()) {
                sendNotificationToUser(bettingMatch, bettingGroupRepository.findOne(bettingGroupId).getPlayers());
            }
        }


        String timeInBettingMatchForm = bettingMatchForm.getDate() + " " + bettingMatchForm.getTime();

        bettingMatch.setMatch(matchRepository.findOne(Long.valueOf(bettingMatchForm.getMatchId())));
        bettingMatch.setBalance1(Double.valueOf(bettingMatchForm.getBalance1()));
        bettingMatch.setBalance2(Double.valueOf(bettingMatchForm.getBalance2()));
        bettingMatch.setBettingAmount(BigDecimal.valueOf(Double.valueOf(bettingMatchForm.getBettingAmount())));
        bettingMatch.setComment(bettingMatchForm.getComment());
        bettingMatch.setExpiryTime(LocalDateTime.parse(timeInBettingMatchForm, DateTimeFormatter.ofPattern(DateTimeFormat.DATETIME.getFormat())));


        if (!bettingMatchForm.getActivate().equals("")) {
            bettingMatch.setActive(true);
        }

        BettingMatch savedBettingMatch = bettingMatchService.createBettingMatch(bettingMatch, Long.parseLong(bettingMatchForm.getGroupId()));
        if (!bettingMatchForm.getActivate().equals("")) {
            sendNotificationToUser(savedBettingMatch, bettingGroupRepository.findOne(bettingGroupId).getPlayers());

        }


        return "bettingmatch/betting-match";
    }

    @GetMapping("/{bettingGroupId}/matches/{bettingMatchId}/bet")
    public String viewBettingMatchDetailForBet(@PathVariable Long bettingMatchId, Model model, BetForm betForm) {
        BettingMatch bettingMatch = bettingMatchRepository.findOne(bettingMatchId);
        Competitor selectedCompetitor = bettingPlayerService.getSelectedCompetitor(authService.getLoginUser().get(), bettingMatch);
        betForm = BetForm.from(bettingMatch, selectedCompetitor);
        model.addAttribute("betForm", betForm);
        model.addAttribute("bettingMatchId", bettingMatchId);
        if (bettingMatch.getExpiryTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("matchExpiredMessage", true);
            return BETTING_MATCH_DETAIL_FOR_BET_FORM;
        }
        return BETTING_MATCH_DETAIL_FOR_BET_FORM;
    }

    @Transactional
    @PostMapping("/{bettingGroupId}/matches/{bettingMatchId}/bet/{betCompetitorId}")
    public String betCompetitor(@PathVariable Long bettingMatchId, @PathVariable Long betCompetitorId, BetForm betForm, Model model) {
        BettingMatch bettingMatch = bettingMatchRepository.findOne(bettingMatchId);
        model.addAttribute("bettingMatchId", bettingMatchId);

        Optional<Competitor> selectedCompetitor = competitorRepository.findOneById(betCompetitorId);

        if (bettingMatch.getExpiryTime().isBefore(LocalDateTime.now())) {
            betForm = BetForm.from(bettingMatch, bettingPlayerService.getSelectedCompetitor(authService.getLoginUser().get(),bettingMatch));

            model.addAttribute("betForm", betForm);
            model.addAttribute("matchExpiredMessage", true);
            return BETTING_MATCH_DETAIL_FOR_BET_FORM;
        }

        BettingPlayer bettingPlayer = bettingPlayerService.addBettingPlayer(bettingMatchId, betCompetitorId, authService.getLoginUser().get());

        bettingMatch = bettingMatchRepository.findOne(bettingMatchId);
        betForm = BetForm.from(bettingMatch, selectedCompetitor.get());
        model.addAttribute("betForm", betForm);

        return BETTING_MATCH_DETAIL_FOR_BET_FORM;
    }


    private void sendNotificationToUser(BettingMatch bettingMatch, List<String> players) {

        List<String> users = players.stream()
            .filter(s -> !EmailValidate.isValidEmail(s))
            .collect(Collectors.toList());

        List<User> currentBettingPlayers = userService.convertListUsernameToListUser(users);
        mailingService.sendNotificationUpdateBettingMatch(bettingMatch, currentBettingPlayers);
    }


}
