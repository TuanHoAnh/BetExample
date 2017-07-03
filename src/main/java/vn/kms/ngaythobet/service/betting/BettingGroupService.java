/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.service.betting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.web.util.EmailValidate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BettingGroupService {

    private final MailingService mailingService;
    private final BettingGroupRepository bettingGroupRepo;
    private final UserRepository userRepo;


    @Autowired
    public BettingGroupService(BettingGroupRepository bettingGroupRepo, MailingService mailingService, UserRepository userRepo) {
        this.bettingGroupRepo = bettingGroupRepo;
        this.mailingService = mailingService;
        this.userRepo = userRepo;
    }

    public void create(BettingGroup bettingGroup) {
        bettingGroupRepo.save(bettingGroup);
        mailingService.sendCreateBettingGroupAsync(bettingGroup);
    }

    public Integer countBettingGroupsByStatus(BettingGroup.Status status) {
        return bettingGroupRepo.countBettingGroupsByStatus(status);
    }

    public Boolean deleteRejectBettingGroup(Long id) {
        Optional<BettingGroup> bettingGroup = bettingGroupRepo.findOneById(id);

        if (!bettingGroup.isPresent())
            return false;

        mailingService.sendNotiRejectCreatingBettingGroup(bettingGroup.get().getModerator(), bettingGroup.get().getName());
        bettingGroupRepo.delete(bettingGroup.get());
        return true;
    }

    public Boolean approveBettingGroup(Long id) {
        BettingGroup bettingGroup = bettingGroupRepo.findOne(id);

        if (bettingGroup == null) {
            return false;
        }

        bettingGroup.setStatus(BettingGroup.Status.DRAFT);
        bettingGroupRepo.save(bettingGroup);
        mailingService.sendNotiApproveCreatingBettingGroup(bettingGroup.getModerator(), bettingGroup);
        return true;
    }

    public void createModRequest(User user, String groupName, Competition competition) {
        BettingGroup bettingGroup = new BettingGroup();
        bettingGroup.setCompetition(competition);
        bettingGroup.setModerator(user);
        bettingGroup.setName(groupName);
        bettingGroup.setStatus(BettingGroup.Status.PENDING);
        if (user.getRole().equals(UserRole.ADMIN)) {
            bettingGroup.setStatus(BettingGroup.Status.DRAFT);
        }

        bettingGroupRepo.save(bettingGroup);
    }

    public BettingGroup getBettingGroupByName(String groupname) {
        return bettingGroupRepo.findOneByName(groupname).orElse(null);
    }

    public BettingGroup getBettingGroupById(Long groupId) {
        return bettingGroupRepo.findBettingGroupById(groupId).orElse(null);
    }

    public void updateBettingGroup(BettingGroup bettingGroup) {
        bettingGroupRepo.save(bettingGroup);
    }


    public BettingGroup reloadPendingEmailToUsername(BettingGroup bettingGroup) {
        List<String> pendingEmails = bettingGroup.getPlayers()
            .stream()
            .filter(EmailValidate::isValidEmail)
            .collect(Collectors.toList());

        List<String> bettingPlayer = new ArrayList<>(bettingGroup.getPlayers());

        for (String email : pendingEmails) {
            Optional<User> user = userRepo.findUserByEmailAndActivated(email, true);
            if (user.isPresent()) {
                bettingPlayer.remove(email);
                bettingPlayer.add(user.get().getUsername());

            }

        }
        bettingGroup.setPlayers(bettingPlayer);
        Collections.sort(bettingGroup.getPlayers(), String.CASE_INSENSITIVE_ORDER);
        return bettingGroup;
    }

    public Page<BettingGroup> getPendingBettingGroups(Integer pageNumber, int pageSize) {
        Integer validPageNumber =  pageNumber;

        if(pageNumber.compareTo(0) <= 0) {
            validPageNumber = 1;
        }

        PageRequest pageRequest =
            new PageRequest(validPageNumber-1, pageSize, Sort.Direction.DESC, "createdAt");
        return bettingGroupRepo.findAllByStatus(BettingGroup.Status.PENDING, pageRequest);
    }
}
