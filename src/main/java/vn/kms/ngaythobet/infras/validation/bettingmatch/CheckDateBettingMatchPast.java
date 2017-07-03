package vn.kms.ngaythobet.infras.validation.bettingmatch;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by congle on 4/24/2017.
 */
@Documented
@Constraint(validatedBy = CheckDateBettingMatchPastValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface CheckDateBettingMatchPast {
    String message() default "{betting.match.form.date.past}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
