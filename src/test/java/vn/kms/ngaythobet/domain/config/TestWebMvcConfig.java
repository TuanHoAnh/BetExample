/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import vn.kms.ngaythobet.infras.security.AuthenticationFailureHandlerImpl;
import vn.kms.ngaythobet.infras.security.AuthenticationSuccessHandlerImpl;
import vn.kms.ngaythobet.infras.security.UserDetailsServiceImpl;

@Configuration
public class TestWebMvcConfig extends WebMvcConfigurerAdapter {
    @Bean
    UserDetailsService testUserDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandlerImpl();
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandlerImpl();
    }

}
