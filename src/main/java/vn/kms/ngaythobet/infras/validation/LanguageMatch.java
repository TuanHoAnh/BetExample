/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by vuongvu on 4/5/2017.
 */
@Documented
@Constraint(validatedBy = LanguageMatchValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface LanguageMatch {
    String message() default "input language tag incorrect!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
