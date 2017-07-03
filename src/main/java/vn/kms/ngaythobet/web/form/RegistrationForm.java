/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.infras.validation.FieldMatch;
import vn.kms.ngaythobet.infras.validation.FieldUnique;
import vn.kms.ngaythobet.infras.validation.LanguageMatch;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;

@Data
@FieldMatch(
    firstField = "password",
    secondField = "confirmPassword",
    message = "{register.validation.confirm-password}")
public class RegistrationForm {

    @Pattern(
        regexp = "[A-Za-z0-9\\._]{3,10}",
        message = "{register.validation.username}")
    @FieldUnique(
        field = "username",
        entity = User.class,
        message = "{register.validation.unique-username}")
    private String username;

    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[_\\W])[A-Za-z_\\d\\W]{6,128}$",
        message = "{register.validation.password.format}")
    private String password;

    private String confirmPassword;

    @Email(message = "{register.validation.email.format}")
    @Length(min = 1, max = 30, message = "{register.validation.email.length}")
    @FieldUnique(
        field = "email",
        entity = User.class,
        message = "{register.validation.email.unique}")
    private String email;

    @Length(min = 1, max = 50, message = "{register.validation.first-name}")
    private String firstName;

    @Length(min = 1, max = 50, message = "{register.validation.last-name}")
    private String lastName;

    @LanguageMatch(message = "{register.validation.language.format}")
    private String languageTag;

    @AssertTrue(message = "{register.validation.agree-term}")
    private boolean agreeTerms;
}
