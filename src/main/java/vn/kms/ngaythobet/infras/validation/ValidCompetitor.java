package vn.kms.ngaythobet.infras.validation;

import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by phuvha on 4/11/2017.
 */
@Documented
@Constraint(validatedBy = ValidCompetitorValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidCompetitor {
    String message() default "{validation.competitor.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
