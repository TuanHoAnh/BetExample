/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.util;

import org.springframework.context.MessageSource;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by vuhtran on 4/3/2017.
 */
public class EmailTemplateWrapper {

    private SpringTemplateEngine templateEngine;

    private String htmlTemplate;

    private String subject;

    private String htmlContent;

    private String receiver;

    private MessageSource messageSource;

    private Locale locale = Locale.getDefault();

    private Map<String, Object> entryValueMap = new HashMap<>();

    public EmailTemplateWrapper setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }

    public EmailTemplateWrapper setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
        return this;
    }

    public EmailTemplateWrapper setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailTemplateWrapper setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        return this;
    }

    public EmailTemplateWrapper setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }



    public void setReceiver (String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver () {
        return receiver;
    }


    public EmailTemplateWrapper setVariable(String variableName, Object variableValue) {
        entryValueMap.put(variableName, variableValue);
        return this;
    }

    public EmailTemplateWrapper make() {

        subject = messageSource.getMessage(subject, null, "", locale);
        Context thymeLeafContext = new Context(locale);

        for (Map.Entry<String, Object> entry : entryValueMap.entrySet()) {
            thymeLeafContext.setVariable(entry.getKey(), entry.getValue());
        }

        htmlContent = templateEngine.process(htmlTemplate, thymeLeafContext);
        return this;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public String getSubject() {
        return subject;
    }
}
