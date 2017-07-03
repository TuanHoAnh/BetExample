package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.infras.validation.FieldMatch;
import vn.kms.ngaythobet.infras.validation.ValidAddScore;
import vn.kms.ngaythobet.infras.validation.ValidCompetitorsMatch;
import vn.kms.ngaythobet.infras.validation.bettingmatch.FieldDate;
import vn.kms.ngaythobet.infras.validation.bettingmatch.FieldTime;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by tuanho on 4/12/2017.
 */
@Data
@ValidCompetitorsMatch(competitor1Id = "competitor1Id", competitor2Id = "competitor2Id", round = "round", matchId = "id")
@ValidAddScore(matchDate = "matchDate", matchTime = "matchTime", score1 = "score1", score2 = "score2")
public class CreateMatchForm {

    private Long id;

    @NotEmpty(message = "{create.match.round.empty}")
    private String round;

    @NotNull(message = "{create.match.com1.invalid}")
    private Long competitor1Id;

    @NotNull(message = "{create.match.com2.invalid}")
    private Long competitor2Id;

    @Max(value = 1000,message = "{create.match.score1.invalid}")
    private Integer score1;

    @Max(value = 1000,message = "{create.match.score2.invalid}")
    private Integer score2;

    private Long competitionId;

    @NotEmpty(message = "{create.match.date.invalid}")
    @FieldDate
    private String matchDate;

    @NotEmpty(message = "{create.match.time.invalid}")
    @FieldTime
    private String matchTime;

    private String location;

    private Competitor competitor1;
    private Competitor competitor2;
    private Competition competition;
    private LocalDateTime startTime;
}
