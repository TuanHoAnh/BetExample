/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation.bettinggroup;

import vn.kms.ngaythobet.domain.common.BaseEntity;

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
@Constraint(validatedBy = ValidListUpdateGroupPlayerValidator.class)
@Documented
@ReportAsSingleViolation
public @interface ValidListUpdateGroupPlayer {
    String message() default "{validation.betting.group.name.notvalid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String currentGroupId();

    String field();

    Class<? extends BaseEntity> entity();
}
