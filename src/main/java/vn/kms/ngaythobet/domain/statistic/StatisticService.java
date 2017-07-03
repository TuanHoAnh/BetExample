/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.kms.ngaythobet.domain.betting.*;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private BettingMatchRepository bettingMatchRepository;
    private UserRepository userRepository;
    private BettingGroupRepository bettingGroupRepository;

    @Autowired
    public StatisticService(BettingMatchRepository bettingMatchRepository, UserRepository userRepository, BettingGroupRepository bettingGroupRepository) {
        this.bettingMatchRepository = bettingMatchRepository;
        this.userRepository = userRepository;
        this.bettingGroupRepository = bettingGroupRepository;
    }

    public List<BettingPlayer> getListBettingPlayerInGroup(Long groupId) {
        List<BettingPlayer> bettingPlayers = new ArrayList<>();
        List<BettingMatch> bettingMatches = bettingMatchRepository.findByBettingGroupId(groupId);
        bettingMatches.forEach(bettingMatch ->
            addPlayersWhoNotBettingToListBettingPlayer(groupId, bettingMatch).forEach(bettingPlayers :: add));
        return bettingPlayers.stream()
            .sorted(Comparator.comparing(BettingPlayer :: getFullName))
            .collect(Collectors.toList());
    }

    private List<String> getListUserNameInGroup(Long groupId) {
        Optional<BettingGroup> bettingGroup = bettingGroupRepository.findOneById(groupId);
        if (bettingGroup.isPresent()) {
            return bettingGroup.get().getPlayers().stream()
                .filter(p -> !p.contains("@"))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<String> convertListBettingPlayerToListUserName(List<BettingPlayer> currentListBettingPlayer) {
        List<String> listUserName = new ArrayList<>();
        currentListBettingPlayer.forEach(bettingPlayer -> listUserName.add(bettingPlayer.getPlayer().getUsername()));
        return listUserName;
    }

    private List<String> filterWhoNotBetting(Long groupId, BettingMatch bettingMatch) {
        List<String> listUserName = getListUserNameInGroup(groupId);
        List<String> listUserNameBetting = convertListBettingPlayerToListUserName(bettingMatch.getBettingPlayers());
        return listUserName.stream()
            .filter(p -> !listUserNameBetting.contains(p))
            .collect(Collectors.toList());
    }

    private List<BettingPlayer> addPlayersWhoNotBettingToListBettingPlayer(Long groupId, BettingMatch bettingMatch) {
        List<BettingPlayer> currentListBettingPlayer = bettingMatch.getBettingPlayers();
        List<String> listUserNotBetting = filterWhoNotBetting(groupId, bettingMatch);
        listUserNotBetting.forEach(userName -> {
            User user = userRepository.findOneByUsername(userName).orElse(null);
            BettingPlayer bettingPlayer = new BettingPlayer();
            bettingPlayer.setBetCompetitor(null);
            bettingPlayer.setPlayer(user);
            bettingPlayer.setBettingMatch(bettingMatch);
            currentListBettingPlayer.add(bettingPlayer);
        });
        return currentListBettingPlayer;
    }

    private Map<User, BigDecimal> generateLossAmountPerPlayer(Long groupId) {
        List<BettingPlayer> bettingPlayers = getListBettingPlayerInGroup(groupId);
        return bettingPlayers.stream()
            .collect(Collectors.groupingBy(BettingPlayer :: getPlayer,
                Collectors.mapping(BettingPlayer :: calculateLossAmount,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    public Map<String, BigDecimal> totalLossAmountByName(Long groupId) {
        Map<String, BigDecimal> resultLossByName = new HashMap<>();
        generateLossAmountPerPlayer(groupId).forEach((user, bigDecimal) ->
            resultLossByName.put(user.getFirstName() + " \n" + user.getLastName(), bigDecimal));
        return resultLossByName;
    }
}
