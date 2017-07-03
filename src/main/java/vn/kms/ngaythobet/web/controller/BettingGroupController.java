/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingMatch;
import vn.kms.ngaythobet.domain.betting.BettingMatchRepository;
import vn.kms.ngaythobet.domain.betting.BettingPlayer;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.common.MessageService;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.competition.Match;
import vn.kms.ngaythobet.domain.statistic.StatisticService;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.domain.util.RoundFormFilter;
import vn.kms.ngaythobet.service.betting.BettingGroupService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.form.BettingGroupEditorForm;
import vn.kms.ngaythobet.web.form.CreateBettingGroupForm;
import vn.kms.ngaythobet.web.form.CreateModeratorRequestForm;
import vn.kms.ngaythobet.web.util.DataResponse;
import vn.kms.ngaythobet.web.util.EmailValidate;
import vn.kms.ngaythobet.web.util.Pair;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller

@RequestMapping("/competitions/{aliasKey}/bettings")
public class BettingGroupController {

    private static final String REGEX_USER = ",";

    private static final String ALIAS_KEY = "aliasKey";

    private static final String BETTING_GROUP = "bettingGroup";

    private static final String CREATE_BETTING_GROUP_FORM = "content/create-betting-group-form :: create-betting-group";

    @Value("${app.request-approve-betting-groups.page-size}")
    private int pageSize = 10;

    private BettingGroupService bettingGroupService;

    private UserRepository userRepo;

    private MessageService messageService;

    private AuthService authService;

    private CompetitionRepository competitionRepo;

    private MailingService mailingService;

    private UserService userService;

    private StatisticService statisticService;

    private BettingMatchRepository bettingMatchRepo;

    @Autowired
    public BettingGroupController(BettingGroupService bettingGroupService, AuthService authService,
                                  MailingService mailingService, UserRepository userRepo,
                                  StatisticService statisticService, CompetitionRepository competitionRepo,
                                  UserService userService, BettingMatchRepository bettingMatchRepo,
                                  MessageService messageService) {
        this.bettingGroupService = bettingGroupService;
        this.authService = authService;
        this.mailingService = mailingService;
        this.userRepo = userRepo;
        this.competitionRepo = competitionRepo;
        this.userService = userService;
        this.messageService = messageService;
        this.statisticService = statisticService;
        this.bettingMatchRepo = bettingMatchRepo;
    }


    @GetMapping("/")
    public String viewCompetition(@PathVariable String aliasKey, Model model) {
        model.addAttribute("competitionAliasKey", aliasKey);
        return "betting/competition";
    }

    @GetMapping("/create")
    public String viewCreateBettingGroup(CreateBettingGroupForm createBettingGroupForm) {
        return CREATE_BETTING_GROUP_FORM;
    }

    @GetMapping("/moderators")
    @ResponseBody
    public List<User> getModerators(@RequestParam String username) {
        List<User> partUserList = new ArrayList<>();
        List<User> userList = userService.findByUsernameKeyword(username).stream()
            .filter(User::isActivated)
            .collect(Collectors.toList());

        for (User user : userList) {
            User partUser = new User();
            partUser.setId(user.getId());
            partUser.setUsername(user.getUsername());
            partUserList.add(partUser);
        }
        return partUserList;
    }

    @PostMapping("/create")
    public String createBettingGroup(@Valid @ModelAttribute("createBettingGroupForm") CreateBettingGroupForm createBettingGroupForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return CREATE_BETTING_GROUP_FORM;
        }
        BettingGroup rawBettingGroup = new BettingGroup();
        rawBettingGroup.setName(createBettingGroupForm.getName());

        Competition competition = new Competition();
        Optional<Competition> competitionOption = competitionRepo.findOneByAliasKey(createBettingGroupForm.getCompetitionAliasKey());
        if (competitionOption.isPresent()) {
            competition = competitionOption.get();
        }
        rawBettingGroup.setCompetition(competition);

        User moderator = userRepo.findOne(createBettingGroupForm.getModeratorId());
        moderator.setId(createBettingGroupForm.getModeratorId());
        rawBettingGroup.setModerator(moderator);
        rawBettingGroup.setStatus(BettingGroup.Status.DRAFT);
        bettingGroupService.create(rawBettingGroup);
        return CREATE_BETTING_GROUP_FORM;
    }

    @GetMapping("/create-mod-request")
    public Object modRequest(@PathVariable String aliasKey, CreateModeratorRequestForm createModeratorRequestForm) {
        ModelAndView modelAndView = new ModelAndView("betting/moderator-request");
        Competition competition = competitionRepo.findOneByAliasKey(aliasKey).get();
        modelAndView.addObject("competitionId", competition.getId());
        modelAndView.addObject(ALIAS_KEY, aliasKey);
        return modelAndView;
    }

    @PostMapping(value = "/submit-request")
    public String submitRequest(@Valid @ModelAttribute("createModeratorRequestForm") CreateModeratorRequestForm createModeratorRequestForm,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allErrors", bindingResult.getAllErrors());
            return "competition/view::createModeratorRequestFragment";
        }
        authService.getLoginUser().ifPresent(user -> {
            Competition competition = competitionRepo.findOne(createModeratorRequestForm.getCompetitionId());
            bettingGroupService.createModRequest(user, createModeratorRequestForm.getBettingGroupName(), competition);
        });
        return "competition/view::createModeratorRequestFragment";
    }

    @GetMapping("/count-pending-groups")
    @ResponseBody
    public DataResponse<Integer> countBettingGroupPending() {
        return new DataResponse<>(bettingGroupService.countBettingGroupsByStatus(BettingGroup.Status.PENDING));
    }

    @GetMapping("/pending-groups")
    public String getBettingGroupPendingsPage(@RequestParam("pages") Integer pageNumber, Model model) {
        Page<BettingGroup> page = bettingGroupService.getPendingBettingGroups(pageNumber, pageSize);

        int currentPage = page.getNumber() + 1;
        int beginPage = Math.max(1, currentPage - 5);
        int endPage = Math.min(beginPage + 10, page.getTotalPages());

        model.addAttribute("page", page);
        model.addAttribute("beginPage", beginPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalBettingGroups", page.getTotalElements());
        model.addAttribute("pageSize", pageSize);

        return "content/request-betting-group-pageable";
    }

    @DeleteMapping("/request/{bettingGroupsId}")
    @ResponseBody
    public DataResponse<Boolean> deleteBettingGroup(@PathVariable Long bettingGroupsId) {
        return new DataResponse<>(bettingGroupService.deleteRejectBettingGroup(bettingGroupsId));
    }

    @PutMapping("/request/{bettingGroupId}")
    @ResponseBody
    public DataResponse<Pair<Boolean, String>> approveBettingGroup(@PathVariable Long bettingGroupId) {
        return new DataResponse<>(new Pair<>(bettingGroupService.approveBettingGroup(bettingGroupId),
            messageService.getMessage("admin.dashboard.popup.table.status.draft")));
    }

    @GetMapping("/{groupId}")
    public String editBettingGroup(@PathVariable Long groupId, @PathVariable String aliasKey, Model model) {

        model.addAttribute(ALIAS_KEY, aliasKey);
        model.addAttribute("groupId", groupId);
        return "betting/group-detail";
    }

    @GetMapping("/{groupId}/reload")
    public String reloadBettingGroup(@PathVariable Long groupId, @PathVariable String aliasKey, Model model) {

        BettingGroup reloadedBettingGroup = bettingGroupService.reloadPendingEmailToUsername(bettingGroupService.getBettingGroupById(groupId));
        authService.getLoginUser()
            .filter(user -> user.getUsername().equals(reloadedBettingGroup.getModerator().getUsername()))
            .ifPresent(user -> model.addAttribute("isMod", true));

        List<BettingMatch> bettingMatches = bettingMatchRepo.findByBettingGroupId(groupId);
        List<Match> matches = convertBettingMatchListToMatchList(bettingMatches);

        String username = authService.getLoginUser().get().getUsername();
        List<String> users = reloadedBettingGroup.getPlayers();
        if(users.contains(username)) {
            model.addAttribute("isMember", true);
        } else {
            model.addAttribute("isMember", false);
        }

        Integer numberOfPlayers = reloadedBettingGroup.getPlayers().size();
        model.addAttribute("numberOfPlayers", numberOfPlayers);
        String moderatorName = reloadedBettingGroup.getModerator().getUsername();
        model.addAttribute("moderatorName", moderatorName);

        model.addAttribute(ALIAS_KEY, aliasKey);
        model.addAttribute(BETTING_GROUP, reloadedBettingGroup);
        model.addAttribute("bettingMatches", bettingMatches);
        model.addAttribute("matchRounds", RoundFormFilter.filterByRound(reloadedBettingGroup.getCompetition(), bettingMatches));
        return "content/group-detail-form :: group-content-form";
    }

    private List<Match> convertBettingMatchListToMatchList(List<BettingMatch> bettingMatchList) {
        List<Match> matches = new ArrayList<>();
        bettingMatchList.forEach(bettingMatch -> matches.add(bettingMatch.getMatch()));
        return  matches;
    }

    @RequestMapping(value = "/{groupId}/update", method = RequestMethod.POST)
    public String updateBettingGroup(@Valid @ModelAttribute("bettingGroupEditorForm") BettingGroupEditorForm bettingGroupEditorForm,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "content/update-group-form :: update-group-body";
        }

        BettingGroup bettingGroup = bettingGroupService.getBettingGroupById(bettingGroupEditorForm.getId());
        bettingGroup.setId(bettingGroupEditorForm.getId());
        bettingGroup.setName(bettingGroupEditorForm.getGroupName());
        bettingGroup.setRules(bettingGroupEditorForm.getRules());


        if (bettingGroup.getStatus().equals(BettingGroup.Status.PUBLISHED)) {
            List<String> newGroupPlayers = convertListStringPlayerToListStringPlayer(bettingGroupEditorForm.getPlayers());
            List<String> currentPlayers = bettingGroup.getPlayers();

            newGroupPlayers.removeAll(currentPlayers);

            mailingService.sendNotificationToBettingGroupPlayersAsync(bettingGroup, convertToListUser(newGroupPlayers));
            mailingService.sendNotificationToAllEmailInBettingGroupAsync(bettingGroup, getEmailsFromListPlayer(newGroupPlayers));

        }

        List<String> players = convertListStringPlayerToListStringPlayer(bettingGroupEditorForm.getPlayers());

        List<String> lowerCaseEmailListPlayer = players.stream()
            .map(player -> {
                    if (player.contains("@")) {
                        return player.toLowerCase();
                    }
                    return player;
                }
            )
            .collect(Collectors.toList());

        bettingGroup.setPlayers(lowerCaseEmailListPlayer);


        bettingGroupService.updateBettingGroup(bettingGroup);

        return "content/update-group-form :: update-group-body";
    }

    @RequestMapping(value = "/{groupId}/update", method = RequestMethod.GET)
    public String updateBettingGroupView(@Valid @PathVariable Long groupId, Model model) {

        model.addAttribute("bettingGroupEditorForm", BettingGroupEditorForm.from(bettingGroupService.getBettingGroupById(groupId)));
        return "content/update-group-form :: update-group-form";
    }

    @PostMapping("/{groupId}/publish")
    public String publishBetGroup(@PathVariable Long groupId, Model model) {

        BettingGroup bettingGroup = bettingGroupService.getBettingGroupById(groupId);
        authService.getLoginUser()
            .filter(user -> user.getUsername().equals(bettingGroup.getModerator().getUsername()))
            .ifPresent(user -> model.addAttribute("isMod", true));
        Integer numberOfPlayers = bettingGroup.getPlayers().size();
        model.addAttribute("numberOfPlayers", numberOfPlayers);
        String moderatorName = bettingGroup.getModerator().getUsername();
        model.addAttribute("moderatorName", moderatorName);
        Optional<User> currentLoggedInUser = authService.getLoginUser();
        if (!currentLoggedInUser.isPresent())
            return "redirect:/signin";
        if (!currentLoggedInUser.get().getUsername().equals(bettingGroup.getModerator().getUsername())
            || !bettingGroup.getStatus().equals(BettingGroup.Status.DRAFT)
            || bettingGroup.getPlayers().isEmpty()) {
            model.addAttribute("isDraft", true);
            model.addAttribute(BETTING_GROUP, bettingGroup);
            return "content/group-detail-form :: group-detail-body";
        }
        bettingGroup.setStatus(BettingGroup.Status.PUBLISHED);
        bettingGroupService.updateBettingGroup(bettingGroup);


        mailingService.sendNotificationToBettingGroupPlayersAsync(bettingGroup, convertToListUser(bettingGroup.getPlayers()));
        List<String> emailPlayers = bettingGroup.getPlayers()
            .stream()
            .filter(EmailValidate::isValidEmail)
            .collect(Collectors.toList());
        mailingService.sendNotificationToAllEmailInBettingGroupAsync(bettingGroup, emailPlayers);

        model.addAttribute(BETTING_GROUP, bettingGroup);
        return "content/group-detail-form :: group-detail-body";
    }

    private List<User> convertToListUser(List<String> listUsername) {

        return userService.convertListUsernameToListUser(listUsername);
    }

    private List<String> convertListStringPlayerToListStringPlayer(String delimiterPlayer) {

        return new ArrayList<>(Arrays.asList(delimiterPlayer.split(REGEX_USER)));
    }

    private List<String> getEmailsFromListPlayer(List<String> delimiterPlayer) {
        return delimiterPlayer
            .stream()
            .filter(EmailValidate::isValidEmail)
            .collect(Collectors.toList());

    }

    @GetMapping("/{groupId}/statistic-betting-players")
    public String statisticBettingPlayers(@PathVariable(ALIAS_KEY) String aliasKey, @PathVariable("groupId") Long groupId, Model model){
        User currentUser = authService.getLoginUser().orElse(null);
        if (currentUser == null){
            return "redirect:/signin";
        }
        if (!canAccessStatistic(groupId, currentUser)){
            return "403 :: content-body";
        }
        List<BettingPlayer> bettingPlayersGroup = statisticService.getListBettingPlayerInGroup(groupId);
        model.addAttribute("bettingPlayersGroup", bettingPlayersGroup);
        model.addAttribute("bettingGroupName", bettingGroupService.getBettingGroupById(groupId).getName());
        model.addAttribute(ALIAS_KEY, aliasKey);
        model.addAttribute("groupId", groupId);
        return "statistic/statistic-betting-player :: statistic-content";
    }

    @GetMapping("/{groupId}/statistic-betting-players/total-amount-players")
    @ResponseBody
    public DataResponse<Map<String, BigDecimal>> totalAmountPlayerChart(@PathVariable Long groupId){
        return new DataResponse<>(true, statisticService.totalLossAmountByName(groupId));
    }

    private boolean canAccessStatistic(Long groupId, User currentUser){
        return bettingGroupService.getBettingGroupById(groupId).getModerator().getId().equals(currentUser.getId())
            || bettingGroupService.getBettingGroupById(groupId).getPlayers().contains(currentUser.getUsername())
            || currentUser.getRole().equals(UserRole.ADMIN);
    }
}

