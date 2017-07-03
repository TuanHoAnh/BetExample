/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import vn.kms.ngaythobet.service.competition.MatchService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidAddScoreValidator implements ConstraintValidator<ValidAddScore, Object> {

    @Autowired
    private MatchService matchService;

    private String matchDateField;
    private String matchTimeField;
    private String score1Field;
    private String score2Field;
    private String message;


    @Override
    public void initialize(final ValidAddScore constraintAnnotation) {
        matchDateField = constraintAnnotation.matchDate();
        matchTimeField = constraintAnnotation.matchTime();
        score1Field = constraintAnnotation.score1();
        score2Field = constraintAnnotation.score2();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            if ("".equals(FieldUtils.getFieldValue(value, matchDateField)) || "".equals(FieldUtils.getFieldValue(value, matchTimeField))
                || (FieldUtils.getFieldValue(value, score1Field) == null && FieldUtils.getFieldValue(value, score2Field) == null)) {
                return true;
            }
            String matchDate = FieldUtils.getFieldValue(value, matchDateField).toString();
            String matchTime = FieldUtils.getFieldValue(value, matchTimeField).toString();

            if (!matchService.isValidToCreateScore(matchDate, matchTime)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(score1Field)
                    .addConstraintViolation();
                return false;
            }
            return true;
        } catch (Exception ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{create.match.date.invalid}")
                .addPropertyNode(matchDateField)
                .addConstraintViolation();
            return false;
        }
    }
}
