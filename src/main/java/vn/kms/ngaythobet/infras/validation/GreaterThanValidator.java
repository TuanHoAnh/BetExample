package vn.kms.ngaythobet.infras.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Object> {
    private int value;
    private String message;
    @Override
    public void initialize(GreaterThan greaterThan) {
        value=greaterThan.value();
        message=greaterThan.message();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return (Integer.parseInt(o.toString()) >= value);
    }
}
