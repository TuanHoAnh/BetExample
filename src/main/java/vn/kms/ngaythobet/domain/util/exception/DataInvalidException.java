/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.util.exception;

public class DataInvalidException extends NtbException {
    public DataInvalidException(String messageKey, String... messageArgs) {
        super(messageKey, messageArgs);
    }
}
