/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.infras.validation.bettinggroup.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@ValidEditGroupName(
    field = "name",
    entity = BettingGroup.class,
    message = "{updatebettinggroup.error.uniquegroupname}",
    newGroupName = "groupName",
    currentGroupId = "id"
)
@ValidListUpdateGroupPlayer(
    field = "players",
    entity = BettingGroup.class,
    message = "{updatebettinggroup.error.validplayers}",
    currentGroupId = "id"
)
@AddNewPlayer(
    field = "username",
    entity = User.class,
    message = "{updatebettinggroup.error.playernotfound}",
    players = "players",
    currentGroupId = "id"
)
@ValidBettingGroupPlayerEmailExisting(
    currentGroupId = "id",
    players = "players",
    message = "{updatebettinggroup.error.email.same.username}")

public class BettingGroupEditorForm {

    static public BettingGroupEditorForm from(BettingGroup bettingGroup) {
        BettingGroupEditorForm bettingGroupEditorForm = new BettingGroupEditorForm();

        bettingGroupEditorForm.setId(bettingGroup.getId());
        bettingGroupEditorForm.setGroupName(bettingGroup.getName());
        bettingGroupEditorForm.setRules(bettingGroup.getRules());
        bettingGroupEditorForm.setModerator(bettingGroup.getModerator().getUsername());
        bettingGroupEditorForm.setId(bettingGroup.getId());
        bettingGroupEditorForm.setStatus(bettingGroup.getStatus().name());
        bettingGroupEditorForm.setPlayers(String.join(",", bettingGroup.getPlayers()));

        return bettingGroupEditorForm;
    }

    @NotNull
    @HaveModeratorRole(entity = BettingGroup.class, message = "{updatebettinggroup.error.nomoderole}")
    private Long id;

    @NotEmpty(message = "{updatebettinggroup.error.groupnameempty}")
    @Pattern(
        regexp = "[A-Za-z0-9\\._]{3,10}",
        message = "{updatebettinggroup.error.name}")
    private String groupName;

    @Length(max = 1000, message = "{updatebettinggroup.error.length}")
    private String rules;

    @NotEmpty(message = "{updatebettinggroup.error.playersempty}")
    private String players;

    @NotEmpty(message = "{updatebettinggroup.error.empty}")
    private String moderator;

    private String status;

}
