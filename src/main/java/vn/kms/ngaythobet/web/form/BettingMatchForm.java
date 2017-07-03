package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.infras.validation.CheckExpireTimeBettingMatch;
import vn.kms.ngaythobet.infras.validation.FieldActivateBettingMatch;
import vn.kms.ngaythobet.infras.validation.FieldUniqueBettingMatch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import vn.kms.ngaythobet.domain.betting.BettingMatch;
import vn.kms.ngaythobet.infras.validation.ValidChangePassword;
import vn.kms.ngaythobet.infras.validation.bettingmatch.CheckDateBettingMatchPast;
import vn.kms.ngaythobet.infras.validation.bettingmatch.FieldDate;
import vn.kms.ngaythobet.infras.validation.bettingmatch.FieldTime;
import vn.kms.ngaythobet.web.util.DateTimeFormat;

import javax.validation.constraints.Pattern;

/**
 * Created by congle on 4/10/2017.
 */
@Data
@FieldUniqueBettingMatch(entity = BettingMatch.class,
    groupId = "groupId",
    matchId = "matchId",
    bettingMatchId = "id",
    message = "{create.betting.match.fail}"
)

public class BettingMatchForm {

    @NotEmpty(message = "{betting.match.form.match}")
    private String matchId;

    @NotEmpty(message = "{betting.match.form.empty}")
    private String groupId;

    @NotEmpty(message = "{betting.match.form.balance}")
    @Pattern(regexp = "^[+]?([0-9]{1,2})*(|[.](([|0|5]{1,1})|25))?$",
        message = "{betting.match.form.validate.balance}")
    private String balance1;

    @NotEmpty(message = "{betting.match.form.balance}")
    @Pattern(regexp = "^[+]?([0-9]{1,2})*(|[.](([|0|5]{1,1})|25))?$",
        message = "{betting.match.form.validate.balance}")
    private String balance2;

    @NotEmpty(message = "{betting.match.form.amount}")
    @Pattern(regexp = "^[+]?([0-9]{1,10})?$", message = "{betting.match.form.validate.amount}")
    private String bettingAmount;

    @FieldDate(message = "{betting.match.form.validate.date}")
    @NotEmpty(message = "{betting.match.form.date}")
    @CheckDateBettingMatchPast(message = "{betting.match.validate.date.past}")
    private String date;

    @FieldTime(message = "{betting.match.form.validate.time}")
    @NotEmpty(message = "{betting.match.form.time}")
    private String time;

    @CheckExpireTimeBettingMatch(message = "{betting.match.form.expire.time}")
    private String id;

    private String round;

    private String comment;

    private String activate;

    public static BettingMatchForm from(BettingMatch bettingMatch) {
        BettingMatchForm bettingMatchForm = new BettingMatchForm();
        bettingMatchForm.setId(String.valueOf(bettingMatch.getId()));
        bettingMatchForm.setBalance1(String.valueOf(bettingMatch.getBalance1()));
        bettingMatchForm.setBalance2(String.valueOf(bettingMatch.getBalance2()));
        bettingMatchForm.setBettingAmount(bettingMatch.getBettingAmount().stripTrailingZeros().toPlainString());
        bettingMatchForm.setComment(bettingMatch.getComment());
        bettingMatchForm.setGroupId(String.valueOf(bettingMatch.getBettingGroup().getId()));
        bettingMatchForm.setMatchId(String.valueOf(bettingMatch.getMatch().getId()));
        bettingMatchForm.setRound(bettingMatch.getMatch().getRound());

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(DateTimeFormat.DATE.getFormat());
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(DateTimeFormat.TIME.getFormat());
        String formatDate = bettingMatch.getExpiryTime().format(formatterDate);
        String formatTime = bettingMatch.getExpiryTime().format(formatterTime);

        bettingMatchForm.setDate(formatDate);
        bettingMatchForm.setTime(formatTime);

        return bettingMatchForm;
    }


}
