package vn.kms.ngaythobet.infras.validation.bettingmatch;

import vn.kms.ngaythobet.web.util.DateTimeFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by congle on 4/24/2017.
 */
public class CheckDateBettingMatchPastValidator implements ConstraintValidator<CheckDateBettingMatchPast, String> {
    private String message;

    @Override
    public void initialize(CheckDateBettingMatchPast constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(DateTimeFormat.DATE.getFormat());
        LocalDate dateTime = LocalDate.parse(value, formatterDate);
        if (dateTime.isBefore(LocalDate.now())){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
            return false;
        }
        return true;
    }
}
