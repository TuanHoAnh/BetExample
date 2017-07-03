package vn.kms.ngaythobet.infras.validation.bettinggroup;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidBettingGroupPlayerEmailExistingValidator.class)
@Documented
@ReportAsSingleViolation
public @interface ValidBettingGroupPlayerEmailExisting {
    String message() default "{create.competition.rounds.are.duplicated}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String currentGroupId();

    String players();
}
