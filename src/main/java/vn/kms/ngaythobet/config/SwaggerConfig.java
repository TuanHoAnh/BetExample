/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@Profile("!utest")
public class SwaggerConfig {

    private final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Autowired
    private AppProperties appProperties;

    @Bean
    public Docket swaggerSpringfoxDocket() {
        StopWatch watch = new StopWatch();
        watch.start();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .genericModelSubstitutes(ResponseEntity.class)
            .forCodeGeneration(true)
            .genericModelSubstitutes(ResponseEntity.class)
            .directModelSubstitute(LocalDate.class, String.class)
            .directModelSubstitute(ZonedDateTime.class, Date.class)
            .directModelSubstitute(LocalDateTime.class, Date.class)
            .select()
            .paths(regex("/api/.*"))
            .build();

        watch.stop();
        logger.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());

        return docket;
    }

    private ApiInfo apiInfo() {
        AppProperties.ApiInfo apiInfo = appProperties.getApiInfo();

        return new ApiInfo(
            apiInfo.getTitle(),
            apiInfo.getDescription(),
            apiInfo.getVersion(),
            "",
            new Contact(apiInfo.getContact(), "", ""),
            apiInfo.getLicense(),
            "");
    }

}
