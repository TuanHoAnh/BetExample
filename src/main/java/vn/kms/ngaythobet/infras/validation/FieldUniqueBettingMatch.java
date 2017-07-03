package vn.kms.ngaythobet.infras.validation;

import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by congle on 4/11/2017.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldUniqueBettingMatchValidator.class)
@Documented
public @interface FieldUniqueBettingMatch {

    String message() default "{validation.fieldUnique.betting.match.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String matchId();

    String groupId();

    String bettingMatchId();

    Class<? extends BaseEntity> entity();
}
