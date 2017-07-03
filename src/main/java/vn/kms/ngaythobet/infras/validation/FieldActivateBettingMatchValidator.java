package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import vn.kms.ngaythobet.service.betting.BettingMatchService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by congle on 4/15/2017.
 */
public class FieldActivateBettingMatchValidator implements ConstraintValidator<FieldActivateBettingMatch, String> {

    @Autowired
    private BettingMatchService bettingMatchService;

    private String message;

    @Override
    public void initialize(FieldActivateBettingMatch constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String bettingMatch = value;
        if ("".equals(bettingMatch)){
          return true;
        }

        if (bettingMatch == null){
            return true;
        }

        boolean valid = bettingMatchService.checkActivateBettingMatch(Long.valueOf(bettingMatch));
        if (valid==false){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        }
        return valid;
    }
}
