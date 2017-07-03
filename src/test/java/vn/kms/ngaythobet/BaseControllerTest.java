package vn.kms.ngaythobet;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRole;

import javax.servlet.Filter;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public abstract class BaseControllerTest {
    protected MockMvc mockMvc;
    @Autowired
    private WebApplicationContext appContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Before
    public void startUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(appContext)
            .apply(springSecurity())
            .addFilter(springSecurityFilterChain)
            .build();

        doStartUp();
    }

    @After
    public void tearDown() {
        doTearDown();
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
    public User getUser(UserRole role){
        User user = new User();
        user.setUsername("admin");
        user.setPassword("");
        user.setRole(role);
        user.setAvatar("avatar.png");
        user.setEmail("testUser@gmail.com");
        user.setFirstName("user");
        user.setLastName("tester");
        user.setActivated(true);
        return user;
    }
    protected void doStartUp() {
        // implemented by sub-classes
        mockLoginUser(getUser(UserRole.USER));

    }

    protected void doTearDown() {
        // implemented by sub-classes
    }

}
