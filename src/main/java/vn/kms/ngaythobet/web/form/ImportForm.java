package vn.kms.ngaythobet.web.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.infras.validation.FieldUnique;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class ImportForm {

    @FieldUnique(
        field = "aliasKey",
        entity = Competition.class,
        message = "{create.competition.unique.key}")
    @Length(max = 8, message = "{create.competition.key.invalid.length}")
    @Pattern(regexp = "^[A-Za-z0-9]*$",message = "{create.competition.key.invalid}" )
    @NotEmpty(message = "{create.competition.key.is.empty}")
    private String aliasKey;

    @NotEmpty(
        message = "{create.competiton.empty.name}"
    )
    @Length(max = 50, message = "{create.competition.name.invalid.length}")
    @FieldUnique(
        field = "name",
        entity = Competition.class,
        message = "{create.competition.unique.name}")
    private String competitionName;

    @NotEmpty(
        message = "{import.validation.empty-competition-id}"
    )
    private String competitionId;
}
