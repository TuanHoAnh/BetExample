package vn.kms.ngaythobet.service.betting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.betting.BettingMatch;
import vn.kms.ngaythobet.domain.betting.BettingMatchRepository;
import vn.kms.ngaythobet.web.form.BettingMatchForm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by congle on 4/10/2017.
 */
@Service
public class BettingMatchService {

    private BettingMatchRepository bettingMatchRepo;

    private BettingGroupRepository bettingGroupRepo;

    @Autowired
    public BettingMatchService(BettingMatchRepository bettingMatchRepo, BettingGroupRepository bettingGroupRepo) {
        this.bettingMatchRepo = bettingMatchRepo;
        this.bettingGroupRepo = bettingGroupRepo;
    }


    public BettingMatch createBettingMatch(BettingMatch bettingMatch, Long groupId) {
        bettingMatch.setBettingGroup(bettingGroupRepo.findOne(groupId));
        return bettingMatchRepo.save(bettingMatch);
    }

    public boolean checkBettingMatchUserInput(Long matchId, Long groupId, Long bettingMatchId) {
        BettingMatch currentBettingMatch = bettingMatchRepo.findOne(bettingMatchId);
        if (currentBettingMatch.getMatch().getId().equals(matchId) && currentBettingMatch.getBettingGroup().getId().equals(groupId)) {
            return true;
        }
        return false;
    }

    public boolean checkUniqueMatch(Long matchId, Long groupId) {
        return bettingMatchRepo.findByBettingGroupIdAndMatchId(groupId, matchId).isEmpty();
    }

    public boolean checkActivateBettingMatch(Long bettingMatchId) {
        BettingMatch bettingMatch = bettingMatchRepo.findOne(bettingMatchId);
        return !bettingMatch.isActive();
    }

    public boolean checkExpireTimeBettingMatch(Long bettingMatchId) {
        BettingMatch bettingMatch = bettingMatchRepo.findOne(bettingMatchId);
        LocalDateTime expireTime = bettingMatch.getExpiryTime();
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expireTime);
    }

}
