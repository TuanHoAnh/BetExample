/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import vn.kms.ngaythobet.service.user.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidResetTokenValidator implements ConstraintValidator<ValidResetToken, Object> {
    @Autowired
    private UserService userService;

    private String message;

    @Override
    public void initialize(final ValidResetToken constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        return userService.validateResetToken(value.toString());
    }
}
