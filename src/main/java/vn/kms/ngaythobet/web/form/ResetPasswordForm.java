/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import vn.kms.ngaythobet.infras.validation.FieldMatch;
import vn.kms.ngaythobet.infras.validation.ValidResetToken;

import javax.validation.constraints.Pattern;

/**
 * Created by thangvtran on 4/5/2017.
 */
@Data
@FieldMatch(firstField = "newPassword", secondField = "confirmPassword")
public class ResetPasswordForm {
    @NotEmpty
    @ValidResetToken
    private String resetToken;

    @NotEmpty(message = "{reset.form.newpassword.empty}")
    @Pattern(
        regexp = "((^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{6,128}$)|)",
        message = "{reset.form.newpassword.message}")
    private String newPassword;

    private String confirmPassword;
}
