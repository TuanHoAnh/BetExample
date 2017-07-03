package vn.kms.ngaythobet.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import vn.kms.ngaythobet.Application;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.user.Language;
import vn.kms.ngaythobet.service.user.UserService;

import java.util.Arrays;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class BettingGroupControllerTest extends BaseTest {
    private static final String TEST_USERNAME = "test123";
    protected MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthService authService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private WebApplicationContext appContext;

    @Override
    protected void doStartUp() {
        LocaleContextHolder.setLocale(new Locale("en"));
        mockLoginUser("user");
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();

        when(userService.getAllowedLanguages(any()))
            .thenReturn(Arrays.asList(new Language("vi", "Vietnamese"), new Language("en", "English")));
    }

    @Test
    public void modRequest() throws Exception {
        this.mockMvc.perform(get("/betting/create-mod-request")
            .with(csrf().asHeader()))
            .andExpect(status().isOk())
            .andExpect(view().name("betting/moderator-request"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void postRequest() throws Exception {
        this.mockMvc.perform(post("/betting/submit-request")
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("betting/moderator-request"));
    }

}
