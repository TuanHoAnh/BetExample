package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.infras.validation.FieldUnique;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by hieptran on 4/10/2017.
 */
@Data
public class CreateModeratorRequestForm {
    @NotEmpty(message = "{betting-group.name.empty}")
    @Pattern(
        regexp = "(([A-Za-z0-9\\._]{3,10})|)",
        message = "{updatebettinggroup.error.name}")
    @FieldUnique(
        entity = BettingGroup.class,
        field = "name",
        message = "{betting-group.validation.unique-name}")
    private String bettingGroupName;

    @NotNull
    private Long competitionId;
}
