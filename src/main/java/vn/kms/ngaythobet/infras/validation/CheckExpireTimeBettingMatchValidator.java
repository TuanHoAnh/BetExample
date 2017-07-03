package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import vn.kms.ngaythobet.infras.validation.CheckExpireTimeBettingMatch;
import vn.kms.ngaythobet.infras.validation.FieldActivateBettingMatch;
import vn.kms.ngaythobet.service.betting.BettingMatchService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by congle on 4/15/2017.
 */
public class CheckExpireTimeBettingMatchValidator implements ConstraintValidator<CheckExpireTimeBettingMatch, String> {

    @Autowired
    private BettingMatchService bettingMatchService;

    private String message;

    @Override
    public void initialize(CheckExpireTimeBettingMatch constraintAnnotation) {
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
        boolean valid = bettingMatchService.checkExpireTimeBettingMatch(Long.valueOf(bettingMatch));
        if (valid==false){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        }
        return valid;
    }
}
