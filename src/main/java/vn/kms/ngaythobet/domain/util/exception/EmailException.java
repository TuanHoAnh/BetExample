/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.util.exception;

public class EmailException extends NtbException {
    public EmailException(String messageKey, String... messageArgs) {
        super(messageKey, messageArgs);
    }
}
