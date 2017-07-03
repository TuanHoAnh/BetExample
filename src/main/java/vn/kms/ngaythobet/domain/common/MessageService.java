package vn.kms.ngaythobet.domain.common;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {
    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, getLocale());
    }

    private Locale getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }

        return locale;
    }
}
