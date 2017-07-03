/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.util.exception;

public class PermissionException extends NtbException {
    public PermissionException(String messageKey, String... messageArgs) {
        super(messageKey, messageArgs);
    }
}
