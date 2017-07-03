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


@Documented
@Constraint(validatedBy = FieldDateValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface FieldDate {
    String message() default "input language tag incorrect!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
