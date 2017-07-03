package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;
import vn.kms.ngaythobet.domain.competition.Competitor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
public class UpdateMatchScoreForm {

    private Long id;

    @Min(value = 0, message = "${update-match-score.form.number}")
    private Integer score1;

    @Min(value = 0, message = "${update-match-score.form.number}")
    private Integer score2;

    private Competitor competitor1;

    private Competitor competitor2;
}
