package vn.kms.ngaythobet.infras.validation.bettingmatch;

import vn.kms.ngaythobet.infras.validation.bettingmatch.FieldDate;
import vn.kms.ngaythobet.web.util.DateTimeFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class FieldDateValidator implements ConstraintValidator<FieldDate, String> {

    private String message;

    @Override
    public void initialize(FieldDate constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext context){

        if ("".equals(fieldValue)){
            return true;
        }

        if (fieldValue == null){
            return true;
        }

        try{
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(DateTimeFormat.DATE.getFormat());
            LocalDate dateTime = LocalDate.parse(fieldValue, formatterDate);

        }
        catch (DateTimeParseException e)
        {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
            return false;
        }
        return true;
    }
}
