package vn.kms.ngaythobet.infras.validation.bettinggroup;

import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by vuhtran on 4/10/2017.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = AddNewPlayerValidator.class)
@Documented
@ReportAsSingleViolation
public @interface AddNewPlayer {


    String message() default "{validation.betting.group.users.notvalid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();

    String currentGroupId();

    Class<? extends BaseEntity> entity();

    String players();
}
