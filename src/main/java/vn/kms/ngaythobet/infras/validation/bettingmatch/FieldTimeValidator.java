package vn.kms.ngaythobet.infras.validation.bettingmatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Created by congle on 4/20/2017.
 */
public class FieldTimeValidator implements ConstraintValidator<FieldTime, String> {

    private String message;

    @Override
    public void initialize(FieldTime constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext context) {

        if ("".equals(fieldValue)) {
            return true;
        }

        if (fieldValue == null) {
            return true;
        }

        try {
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime dateTime = LocalTime.parse(fieldValue, formatterTime);
        } catch (DateTimeParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
            return false;
        }
        return true;

    }
}
