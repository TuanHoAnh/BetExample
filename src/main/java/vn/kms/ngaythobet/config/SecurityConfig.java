/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import vn.kms.ngaythobet.domain.user.UserRole;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Profile("!utest")
@EnableOAuth2Client
@EnableAuthorizationServer
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String PARA_USERNAME = "username";
    public static final String PARA_CREDENTIAL = "password";
    private static final Integer REMEMBER_ME_TIME = 30 * 24 * 60 * 60;

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**", "/images/**", "/css/**", "/js/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/user/register", "/signin*", "/user/activate", "/user/reset-password*", "/sidebar").permitAll()
            .antMatchers("/api/match/update-match-score/*", "/competitor/save", "/competitions/*/bettings/create").hasAuthority(UserRole.ADMIN.getAuthority())
            .antMatchers("/competitions/create-page", "/competitions/*/update", "/competitions/import", "/competitions/auto-import").hasAuthority(UserRole.ADMIN.getAuthority())
            .antMatchers("/competitor/save").hasAuthority(UserRole.ADMIN.getAuthority())
            .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/signin")
                .usernameParameter(PARA_USERNAME)
                .passwordParameter(PARA_CREDENTIAL)
                .failureHandler(authenticationFailureHandler)
                .successHandler(authenticationSuccessHandler)
            .and()
            .logout()
                .logoutUrl("/signout")
                .deleteCookies("remember-me")
                .logoutSuccessUrl("/signin")
            .and()
            .rememberMe()
                .tokenValiditySeconds(REMEMBER_ME_TIME)
            .and()
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }



    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(facebook(), "/signin-facebook"));
        filters.add(ssoFilter(google(), "/signin-google"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter oAuth2ClientFilter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);

        oAuth2ClientFilter.setAuthenticationSuccessHandler(urlOAuth2SuccessRedirect("/user/signin-sso"));
        oAuth2ClientFilter.setAuthenticationFailureHandler(urlOAuth2FailRedirect());
        oAuth2ClientFilter.setRestTemplate(oAuth2RestTemplate);

        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
            client.getClient().getClientId());

        tokenServices.setRestTemplate(oAuth2RestTemplate);
        oAuth2ClientFilter.setTokenServices(tokenServices);

        return oAuth2ClientFilter;
    }

    private SimpleUrlAuthenticationSuccessHandler urlOAuth2SuccessRedirect(String url){
        return new SimpleUrlAuthenticationSuccessHandler(){
            @Override
            public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication)
                throws IOException, ServletException {
                this.setDefaultTargetUrl(url);
                super.onAuthenticationSuccess(req, resp, authentication);
            }
        };

    }

    private SimpleUrlAuthenticationFailureHandler urlOAuth2FailRedirect(){
        return new SimpleUrlAuthenticationFailureHandler(){
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
                request.setAttribute("errorSSO", "error");
                request.getSession().invalidate();
                request.getRequestDispatcher("signin").forward(request,response);
            }
        };

    }
}




