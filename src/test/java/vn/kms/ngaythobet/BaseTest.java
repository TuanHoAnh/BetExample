/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRole;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("utest")
@SpringBootTest(classes = TestConfiguration.class)
public abstract class BaseTest {
    @Before
    public void startUp() {
        doStartUp();
    }

    @After
    public void tearDown() {
        doTearDown();
    }

    protected void mockLoginUser(String username) {
        mockLoginUser(username, UserRole.USER);
    }

    protected void mockLoginUser(String username, UserRole role) {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }

            public String getFullName() {
                return username;
            }

            public LocalDate getRegisteredDate() {
                return LocalDate.MAX;
            }
        };
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, username, AuthorityUtils.createAuthorityList(role.getAuthority()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected Authentication mockLoginUser(User user) {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return user.getFirstName();
            }
            public User getUser(){
                return  user;
            }
            public String getFullName(){
                return user.getFullName();
            }
            public LocalDateTime getRegisteredDate(){
                return user.getCreatedAt();
            }
        };
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            principal,
            user.getEmail(),
            AuthorityUtils.createAuthorityList(user.getRole().getAuthority()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }


    protected void doStartUp() {
        // implemented by sub-classes
    }

    protected void doTearDown() {
        // implemented by sub-classes
    }
}
