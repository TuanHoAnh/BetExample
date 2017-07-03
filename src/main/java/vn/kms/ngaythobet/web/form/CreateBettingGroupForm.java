/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.infras.validation.FieldUnique;
import vn.kms.ngaythobet.infras.validation.FieldUniqueInGroup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@FieldUniqueInGroup(name="name", groupId = "competitionAliasKey")
public class CreateBettingGroupForm {

    private String competitionAliasKey;

    @Pattern(
        regexp = "[A-Za-z0-9\\._]{3,10}",
        message = "{updatebettinggroup.error.name}")
    @NotEmpty(message = "{create.betting.group.name}")
    private  String name;

    @NotNull(message = "{create.betting.group.moderator}")
    private Long moderatorId;

}
