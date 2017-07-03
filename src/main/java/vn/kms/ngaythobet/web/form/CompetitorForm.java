package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.Competitor;
import vn.kms.ngaythobet.infras.repository.StringListConverter;
import vn.kms.ngaythobet.infras.validation.FieldUnique;
import vn.kms.ngaythobet.infras.validation.ValidCompetitor;
import vn.kms.ngaythobet.infras.validation.ValidRounds;

import javax.persistence.Convert;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by phuvha on 4/11/2017.
 */
@Data
@ValidCompetitor(message = "{create.competitor.unique.name}")
public class CompetitorForm {
    private Long id;

    private Long competitionId;

    @Length(min = 1, max = 50, message =  "{create.competitor.empty.name}")
    private String name;

    private String logo;

    private MultipartFile logoFile;

}
