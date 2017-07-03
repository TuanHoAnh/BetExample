/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */
package vn.kms.ngaythobet;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
@ComponentScan(basePackages = {
    "vn.kms.ngaythobet.domain",
    "vn.kms.ngaythobet.config",

    "vn.kms.ngaythobet.service.competition",
    "vn.kms.ngaythobet.service",
    "vn.kms.ngaythobet.infras.dao"})
public class TestConfiguration {
}
