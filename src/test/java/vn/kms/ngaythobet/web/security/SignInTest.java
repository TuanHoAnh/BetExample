/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import vn.kms.ngaythobet.infras.security.AuthenticationFailureHandlerImpl;

import javax.servlet.Filter;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class SignInTest {

    @Autowired
    private WebApplicationContext appContext;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(appContext)
            .addFilters(springSecurityFilterChain)
            .build();
    }

    @Test
    public void authenticationSuccess() throws Exception {
        mockMvc
            .perform(formLogin("/signin")
                .user("username", "user")
                .password("password", "password"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"))
            .andExpect(authenticated().withUsername("user"));
    }

    @Test
    public void accountNotExist() throws Exception {
        mockMvc
            .perform(formLogin("/signin")
                .user("username", "notfound")
                .password("password", "invalid"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/signin?error=not-exist"))
            .andExpect(unauthenticated());
    }

    @Test
    public void sessionAuthenticationFailed() throws Exception {

        HttpSession session  = mockMvc.perform(formLogin("/signin").user("username","notfound").password("password","invalid"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/signin?error=not-exist"))
                .andReturn()
                .getRequest()
                .getSession();

        assertEquals("notfound",(String) session.getAttribute("username"));
        assertEquals("invalid",(String) session.getAttribute("password"));
    }

    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    static class Config extends WebSecurityConfigurerAdapter {

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                .antMatchers("/register", "/signin*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/signin")
                .failureHandler(authenticationFailureHandler());
        }

        @Override
        @Bean
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }

        public AuthenticationFailureHandlerImpl authenticationFailureHandler() {
            return new AuthenticationFailureHandlerImpl();
        }

    }
}
