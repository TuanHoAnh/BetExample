package vn.kms.ngaythobet.web.interceptor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.kms.ngaythobet.config.AppProperties;

@ControllerAdvice(annotations = Controller.class)
public class PropertiesInterceptor {

    private final AppProperties appProperties;

    public PropertiesInterceptor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @ModelAttribute(name = "appVersion")
    public String appVersion() {
        return appProperties.getApiInfo().getVersion();
    }
}
