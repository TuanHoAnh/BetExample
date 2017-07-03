package vn.kms.ngaythobet.infras.validation.bettinggroup;

import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by vuhtran on 4/11/2017.
 */
@Documented
@Constraint(validatedBy = HaveModeratorRoleValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface HaveModeratorRole {

    String message() default "{validation.betting.group.id.noauthorize.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends BaseEntity> entity();


}
