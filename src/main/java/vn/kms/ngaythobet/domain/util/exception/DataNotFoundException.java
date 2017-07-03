/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.util.exception;

public class DataNotFoundException extends NtbException {
    public DataNotFoundException(String messageKey, String... messageArgs) {
        super(messageKey, messageArgs);
    }
}
