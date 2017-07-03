/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.service.user.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidChangePasswordValidator implements ConstraintValidator<ValidChangePassword, Object> {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private String currentPasswordField;
    private String newPasswordField;
    private String message;

    @Override
    public void initialize(final ValidChangePassword constraintAnnotation) {
        currentPasswordField = constraintAnnotation.currentPassword();
        newPasswordField = constraintAnnotation.newPassword();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            String currentPassword = FieldUtils.getFieldValue(value, currentPasswordField).toString();
            String newPassword = FieldUtils.getFieldValue(value, newPasswordField).toString();
            String login = authService.getLogin();


            UserService.ChangePasswordError error = userService.validateChangePassword(login, currentPassword, newPassword);
            if (error == null) {
                return true;
            }

            if (error == UserService.ChangePasswordError.INVALID_CURRENT_PASSWORD) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{change.password.oldpassword.wrong}")
                    .addPropertyNode(currentPasswordField)
                    .addConstraintViolation();
            }

            if (error == UserService.ChangePasswordError.SAME_CURRENT_PASSWORD) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{change.password.newpassword.same.oldpassword}")
                    .addPropertyNode(newPasswordField)
                    .addConstraintViolation();
            }

            return false;
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Could not access form field", ex);
        }
    }
}
