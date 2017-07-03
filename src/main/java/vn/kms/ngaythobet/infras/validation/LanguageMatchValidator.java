/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import vn.kms.ngaythobet.domain.user.Language;
import vn.kms.ngaythobet.service.user.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by vuongvu on 4/5/2017.
 */
public class LanguageMatchValidator implements ConstraintValidator<LanguageMatch, String> {
    @Autowired
    private UserService userService;

    private String message;

    @Override
    public void initialize(LanguageMatch annotation) {
        message = annotation.message();
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext context) {
        boolean valid = userService.getAllowedLanguages(LocaleContextHolder.getLocale()).stream()
            .map(Language::getTag)
            .anyMatch(fieldValue::equals);

        if (valid) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

        return false;
    }
}
