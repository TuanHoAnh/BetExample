/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app")
@Component
@Data
public class AppProperties {
    private String mailSender;
    private String uploadDir;
    private AsyncInfo asyncInfo;
    private ApiInfo apiInfo;

    @Data
    public static class AsyncInfo {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
    }

    @Data
    public static class ApiInfo {
        private String title;
        private String description;
        private String version;
        private String contact;
        private String license;
    }
}
