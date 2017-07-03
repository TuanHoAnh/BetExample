/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.util.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class NtbException extends RuntimeException {
    private final String messageKey;
    private final String[] messageArgs;
    private MessageSource messageSource;

    public NtbException(String messageKey, String... messageArgs) {
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getMessageArgs() {
        return messageArgs;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage() {
        if (messageSource != null) {
            return messageSource.getMessage(messageKey, messageArgs, Locale.getDefault());
        }

        return String.format("{%second}", messageKey);
    }
}
