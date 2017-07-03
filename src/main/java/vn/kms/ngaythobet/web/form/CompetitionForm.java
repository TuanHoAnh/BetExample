package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.infras.repository.StringListConverter;
import vn.kms.ngaythobet.infras.validation.FieldUnique;
import vn.kms.ngaythobet.infras.validation.GreaterThan;
import vn.kms.ngaythobet.infras.validation.ValidRounds;

import javax.persistence.Convert;
import java.io.File;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class CompetitionForm {

    private static final String VALID_FILE_TYPE = ".png,.jpeg,.jpg";

    private String id;

    @FieldUnique(
        field = "aliasKey",
        entity = Competition.class,
        message = "{create.competition.unique.key}"
    )
    @NotEmpty(message = "{create.competition.key.is.empty}")
    @Length(max = 8, message = "{create.competition.key.invalid.length}")
    @Pattern(regexp = "^[A-Za-z0-9]*$",message = "{create.competition.key.invalid}" )
    private String aliasKey;

    @FieldUnique(
        field = "name",
        entity = Competition.class,
        message = "{competition.name.error.duplicate}"

    )
    @Length(max = 50, message = "{create.competition.name.invalid.length}")
    @NotEmpty(message = "{create.competiton.empty.name}")
    private String name;

    @NotEmpty(message = "{create.competition.rounds.is.empty}")
    @ValidRounds
    @Convert(converter = StringListConverter.class)
    private List<String> rounds;

    private MultipartFile logoFile;

    private String logo;

    @GreaterThan(value = 2, message = "{publish.competition.less.than.2.competitor}" )
    private int numberOfCompetitor;

    @GreaterThan(value = 1,message = "{publish.competition.no.matches}")
    private int numberOfMatch;

    private String isPressedPublish;

    private String status;

    public static CompetitionForm copyDataFrom(Competition competition) {
        CompetitionForm competitionForm = new CompetitionForm();
        if(competition == null) {
            return competitionForm;
        }
        competitionForm.setId(competition.getId().toString());
        competitionForm.setName(competition.getName());
        competitionForm.setAliasKey(competition.getAliasKey());
        competitionForm.setRounds(competition.getRounds());
        competitionForm.setStatus(competition.getStatus().name());
        competitionForm.setLogo(competition.getLogo());
        return competitionForm;
    }

}
