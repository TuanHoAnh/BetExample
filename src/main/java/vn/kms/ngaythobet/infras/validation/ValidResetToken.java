/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import javax.validation.Payload;

public @interface ValidResetToken {
    String message() default "{validation.validResetToken.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
