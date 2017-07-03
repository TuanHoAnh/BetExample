package vn.kms.ngaythobet.infras.validation.bettingmatch;

import vn.kms.ngaythobet.infras.validation.LanguageMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by congle on 4/20/2017.
 */
@Documented
@Constraint(validatedBy = FieldTimeValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface FieldTime {
    String message() default "input time incorrect!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
