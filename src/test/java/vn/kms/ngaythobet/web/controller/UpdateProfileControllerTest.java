package vn.kms.ngaythobet.web.controller;

import org.h2.engine.User;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.LocaleResolver;
import vn.kms.ngaythobet.BaseControllerTest;
import vn.kms.ngaythobet.config.AppProperties;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.user.Language;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.service.file.FileService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.interceptor.PropertiesInterceptor;

import java.security.Principal;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;


@WebMvcTest(UserController.class)
public class UpdateProfileControllerTest extends BaseControllerTest {
    private static final String TEST_USERNAME = "test123";

    @MockBean
    private UserService userService;

    @MockBean
    private AppProperties appProperties;

    @MockBean
    private FileService fileService;

    @MockBean
    private AuthService authService;

    @MockBean
    private LocaleResolver localeResolver;

    @MockBean
    private CompetitionRepository competitionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private PropertiesInterceptor propertiesInterceptor;

    @Override
    protected void doStartUp() {
        when(userService.getAllowedLanguages(any()))
            .thenReturn(Arrays.asList(new Language("vi", "Vietnamese"), new Language("en", "English")));

        mockLoginUser(getUser(UserRole.USER));
    }

    @Test
    public void updateUserProfileTest_FirstNameNull() throws Exception {
        this.mockMvc.perform(postUserProfileForm("", "last", "test@test.local", "en"))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "firstName"));
    }

    @Test
    public void updateUserProfileTest_FirstNameTooLong() throws Exception {
        this.mockMvc.perform(postUserProfileForm("bbbbbbbbbb bbbbbbbbbb bbbbbbbbbb bbbbbbbbb bbbbbbbbb",
            "last", "test@test.local", "en"))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "firstName"));
    }

    @Test
    public void updateUserProfileTest_LastNameNull() throws Exception {
        this.mockMvc.perform(postUserProfileForm("first","", "test@test.local", "en"))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "lastName"));
    }

    @Test
    public void updateUserProfileTest_LastNameTooLong() throws Exception {
        this.mockMvc.perform(postUserProfileForm("first",
            "bbbbbbbbbb bbbbbbbbbb bbbbbbbbbb bbbbbbbbb bbbbbbbbb", "test@test.local", "en"))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "lastName"));
    }

    @Test
    public void updateUserProfileTest_ValidField() throws Exception {
        this.mockMvc.perform(postUserProfileForm("first", "last", "test@test.local", "en"))
            .andExpect(status().is(200))
            .andExpect(model().hasNoErrors());
    }

    @Test
    public void updateUserProfileTest_LanguageEmpty() throws Exception {
        this.mockMvc.perform(postUserProfileForm("first", "last", "test@test.local", ""))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "languageTag"));
    }

    @Test
    public void updateUserProfileTest_InvalidLanguage() throws Exception {
        this.mockMvc.perform(postUserProfileForm("first", "last", "test@test.local", "xxx"))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "languageTag"));
    }

    @Test
    public void updateUserProfileTest_InvalidLanguageAndEmptyFirstName() throws Exception {
        this.mockMvc.perform(postUserProfileForm("", "last", "test@test.local", ""))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(2))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "firstName"))
            .andExpect(model().attributeHasFieldErrors("userProfileForm", "languageTag"));
    }

    private MockHttpServletRequestBuilder postUserProfileForm(String firstName, String lastName,
                                                              String email, String languageTag) {
        Principal principal = () -> TEST_USERNAME;

        return post("/user/update-profile")
            .accept(MediaType.MULTIPART_FORM_DATA)
            .param("firstName", firstName)
            .param("lastName", lastName)
            .param("email", email)
            .param("languageTag", languageTag)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            });
    }

}
