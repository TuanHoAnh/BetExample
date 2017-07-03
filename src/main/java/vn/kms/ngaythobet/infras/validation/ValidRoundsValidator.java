package vn.kms.ngaythobet.infras.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidRoundsValidator implements ConstraintValidator<ValidRounds, Object> {
    private String message;

    @Override
    public void initialize(ValidRounds validRounds) {
        message = validRounds.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        return isNotDuplicated((List<String>) value);
    }

    private boolean isNotDuplicated(List<String> rounds) {
        if (rounds.size() == 1) {
            return true;
        }

        for (int i = 0; i < rounds.size(); i++) {
            for (int j = i + 1; j < rounds.size(); j++) {
                if (rounds.get(i).equals(rounds.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
