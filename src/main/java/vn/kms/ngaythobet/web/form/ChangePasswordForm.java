/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.annotation.Order;
import vn.kms.ngaythobet.infras.validation.FieldMatch;
import vn.kms.ngaythobet.infras.validation.ValidChangePassword;

import javax.validation.constraints.Pattern;

/**
 * Created by congle on 4/3/2017.
 */
@Data
@FieldMatch(firstField = "newPassword", secondField = "confirmNewPassword" , message = "{change.password.newpass.newconfirmpass.notmatch}")
@ValidChangePassword(currentPassword = "currentPassword", newPassword = "newPassword")
public class ChangePasswordForm {

    @NotEmpty(message ="{change.password.form.empty}")
    @Pattern(
        regexp = "((^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[_\\W])[A-Za-z_\\d\\W]{6,128}$)|)",
        message = "{change.password.form.validate}")
    private String currentPassword;

    @NotEmpty(message ="{change.password.form.empty}")
    @Pattern(
        regexp = "((^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[_\\W])[A-Za-z_\\d\\W]{6,128}$)|)",
        message = "{change.password.form.validate}")
    private String newPassword;

    @NotEmpty(message ="{change.password.form.empty}")
    private String confirmNewPassword;

}
