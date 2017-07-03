package vn.kms.ngaythobet.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.Application;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.user.Language;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.form.RegistrationForm;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class RegisterUserControllerTest extends BaseTest {
    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext appContext;

    protected MockMvc mockMvc;

    @Before
    public void startUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();
        doStartUp();
    }

    @Override
    protected void doStartUp() {
        when(userService.getAllowedLanguages(any()))
            .thenReturn(Arrays.asList(new Language("vi", "Vietnamese"), new Language("en", "English")));
    }

    @Test
    public void getRegisterForm() throws Exception {
        this.mockMvc.perform(get("/user/register"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/register"));
    }

    @Test
    public void registerSubmit_ValidateSuccess() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setUsername("test1234");
        registrationForm.setPassword("Test@123");
        registrationForm.setConfirmPassword("Test@123");
        registrationForm.setLanguageTag("vi");
        registrationForm.setEmail("test1234@gmail.com");
        registrationForm.setFirstName("Test");
        registrationForm.setLastName("User");
        registrationForm.setAgreeTerms(true);

        this.mockMvc.perform(postRegistrationForm(registrationForm))
            .andExpect(status().is(302))
            .andExpect(redirectedUrl("/user/register?success"));

        verify(userService, times(1)).registerUser(any());
    }

    @Test
    public void registerSubmit_ValidateFail_Username() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setUsername("te");
        registrationForm.setPassword("Test@123");
        registrationForm.setConfirmPassword("Test@123");
        registrationForm.setLanguageTag("vi");
        registrationForm.setEmail("test1234@gmail.com");
        registrationForm.setFirstName("Test");
        registrationForm.setLastName("User");
        registrationForm.setAgreeTerms(true);

        this.mockMvc.perform(postRegistrationForm(registrationForm))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("registrationForm", "username"))
            .andExpect(view().name("user/register"));

        verify(userService, times(0)).registerUser(any());
    }

    @Test
    public void registerSubmit_ValidateFail_UsernamePasswordEmail() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setUsername("te");
        registrationForm.setPassword("Testasdas");
        registrationForm.setConfirmPassword("Testasdas");
        registrationForm.setLanguageTag("vi");
        registrationForm.setEmail("test1234gmail.com");
        registrationForm.setFirstName("Test");
        registrationForm.setLastName("User");
        registrationForm.setAgreeTerms(true);

        this.mockMvc.perform(postRegistrationForm(registrationForm))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("registrationForm", "username", "password", "email"))
            .andExpect(view().name("user/register"));

        verify(userService, times(0)).registerUser(any());
    }

    @Test
    public void registerSubmit_ValidateFail_ConfirmPassword() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setUsername("test1234");
        registrationForm.setPassword("Test@123");
        registrationForm.setConfirmPassword("Test123");
        registrationForm.setLanguageTag("vi");
        registrationForm.setEmail("test1234@gmail.com");
        registrationForm.setFirstName("Test");
        registrationForm.setLastName("User");
        registrationForm.setAgreeTerms(true);

        this.mockMvc.perform(postRegistrationForm(registrationForm))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("registrationForm", "confirmPassword"))
            .andExpect(view().name("user/register"));

        verify(userService, times(0)).registerUser(any());
    }

    private MockHttpServletRequestBuilder postRegistrationForm(RegistrationForm registrationForm) {
        return post("/user/register")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", registrationForm.getUsername())
            .param("password", registrationForm.getPassword())
            .param("confirmPassword", registrationForm.getConfirmPassword())
            .param("email", registrationForm.getEmail())
            .param("firstName", registrationForm.getFirstName())
            .param("lastName", registrationForm.getLastName())
            .param("languageTag", registrationForm.getLanguageTag())
            .param("agreeTerms", "true");
    }
}
