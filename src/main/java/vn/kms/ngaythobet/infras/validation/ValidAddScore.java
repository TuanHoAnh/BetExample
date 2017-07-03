package vn.kms.ngaythobet.infras.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hungptnguyen on 4/13/2017.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidAddScoreValidator.class)
@Documented
public @interface ValidAddScore {
    String message() default "{create.match.addScore.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String matchDate();

    String matchTime();

    String score1();

    String score2();
}
