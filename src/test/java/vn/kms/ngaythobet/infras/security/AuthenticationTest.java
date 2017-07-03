/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */
/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.security;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.TestConfiguration;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest(classes = TestConfiguration.class)
public class AuthenticationTest {
    private static final String TEST_USERNAME = "test";
    private static final String ACTIVATED_USERNAME = "activate";
    private static final String INACTIVATED_USERNAME = "inactivate";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
            .apply(springSecurity())
            .build();
    }

    @Before
    public void startUp() {
        userRepo.save(mockInactivatedUser());
        userRepo.save(mockVietnameseUser());
    }

    @After
    public void tearDown() {
        List<String> userList = Arrays.asList(TEST_USERNAME, INACTIVATED_USERNAME, ACTIVATED_USERNAME);
        for(String username: userList) {
            userRepo
                .findOneByUsername(username)
                .ifPresent(userRepo::delete);
        }
    }

    private User mockRawUser() {
        User user = new User();

        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@test.local");
        user.setLanguageTag("en");
        user.setRole(UserRole.USER);
        user.setActivated(true);

        return user;
    }

    private User mockInactivatedUser() {
        User user = mockRawUser();

        user.setUsername(INACTIVATED_USERNAME);
        user.setEmail(INACTIVATED_USERNAME + "@test.local");
        user.setActivated(false);
        return user;
    }

    private User mockVietnameseUser() {
        User user = mockRawUser();

        user.setUsername(ACTIVATED_USERNAME);
        user.setEmail(ACTIVATED_USERNAME + "@test.local");
        user.setLanguageTag("vi");
        return user;
    }

    @Test
    public void testChangeLanguageSuccess() throws Exception {
        mockMvc
            .perform(formLogin("/signin")
                .user(ACTIVATED_USERNAME)
                .password("password"))
            .andExpect(redirectedUrl("/?lang=vi"));
    }

    @Test
    public void testLoginFailOnInactivatedUser() throws Exception {
        mockMvc
            .perform(formLogin("/signin")
                .user(INACTIVATED_USERNAME)
                .password("password"))
            .andExpect(redirectedUrl("/signin?error=not-activate"));
    }

    @Test
    public void testLoginFailOnWrongPassword() throws Exception {
        mockMvc
            .perform(formLogin("/signin")
                .user(ACTIVATED_USERNAME)
                .password("wrong_password"))
            .andExpect(redirectedUrl("/signin?error=not-exist"));
    }

    @Test
    public void testLoginFailOnWrongUsername() throws Exception {
        mockMvc
            .perform(formLogin("/signin")
                .user("wrong_username")
                .password("password"))
            .andExpect(redirectedUrl("/signin?error=not-exist"));
    }
}
