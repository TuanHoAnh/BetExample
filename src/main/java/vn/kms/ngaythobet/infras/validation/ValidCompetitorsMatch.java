package vn.kms.ngaythobet.infras.validation;

import vn.kms.ngaythobet.domain.competition.Competitor;

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
@Constraint(validatedBy = ValidCompetitorsMatchValidator.class)
@Documented
public @interface ValidCompetitorsMatch {
    String message() default "{create.match.competitors.same}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String competitor1Id();

    String competitor2Id();

    String round();

    String matchId();
}
