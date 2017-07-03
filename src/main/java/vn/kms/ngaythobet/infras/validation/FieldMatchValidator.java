/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.security.util.FieldUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstField;
    private String secondField;
    private String message;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstField = constraintAnnotation.firstField();
        secondField = constraintAnnotation.secondField();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            final Object firstValue = FieldUtils.getFieldValue(value, firstField);
            final Object secondValue = FieldUtils.getFieldValue(value, secondField);

            if (firstValue == null && secondValue == null) {
                return true;
            }

            boolean matched = (firstValue != null && firstValue.equals(secondValue));
            if (!matched) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondField)
                    .addConstraintViolation();
            }

            return matched;
        } catch (Exception ex) {
            throw new RuntimeException("Could not compare field value of " + firstField + " and " + secondField, ex);
        }
    }
}
