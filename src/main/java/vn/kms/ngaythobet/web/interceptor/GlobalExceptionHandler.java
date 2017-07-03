/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public String defaultIOErrorHandler(IOException e) throws Exception {
        return renderErrorPage("404", e);
    }

    @ExceptionHandler(Exception.class)
    public String defaultErrorHandler(Exception e) throws Exception {
        return renderErrorPage("500", e);
    }

    private String renderErrorPage(String view, Exception ex) throws Exception {
        if (isExceptionAnnotatedWithResponseStatus(ex)) {
            throw ex;
        }

        log.error("System Error", ex);

        return view;
    }

    private boolean isExceptionAnnotatedWithResponseStatus(Exception ex) {
        return Objects.nonNull(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class));
    }
}
