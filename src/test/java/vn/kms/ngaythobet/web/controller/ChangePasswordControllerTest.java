package vn.kms.ngaythobet.web.controller;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.LocaleResolver;
import vn.kms.ngaythobet.BaseControllerTest;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.domain.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by congle on 4/5/2017.
 */
@WebMvcTest(UserController.class)
public class ChangePasswordControllerTest extends BaseControllerTest {
    private static final String TEST_USERNAME = "test123";

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private LocaleResolver localeResolver;

    @Test
    public void changePasswordSubmit_CurrentPasswordError() throws Exception {
        mockMvc.perform(postChangePasswordForm("", "User@123", "User@123"))
            .andExpect(status().is(200))
            .andExpect(model().attributeHasFieldErrors("changePasswordForm", "currentPassword"));

    }


    @Test
    public void changePasswordSubmit_InvalidCurrentPassword() throws Exception {
        mockMvc.perform(postChangePasswordForm("abc", "User@123", "User@123"))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(model().attributeHasFieldErrors("changePasswordForm", "currentPassword"));
    }

    @Test
    public void changePasswordSubmit_NewPasswordEmpty() throws Exception {
        mockMvc.perform(postChangePasswordForm("User@123", "", "User@123"))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(model().attributeHasFieldErrors("changePasswordForm", "newPassword"));
    }

    @Test
    public void changePasswordSubmit_InvalidNewPassword() throws Exception {
        mockMvc.perform(postChangePasswordForm("User@123", "abc", "User@123"))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(model().attributeHasFieldErrors("changePasswordForm", "newPassword"));
    }

    @Test
    public void changePasswordSubmit_ConfirnNewPassWordEmpty() throws Exception {
        mockMvc.perform(postChangePasswordForm("User@123", "Admin@123", ""))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(model().attributeHasFieldErrors("changePasswordForm", "confirmNewPassword"));
    }

    @Test
    public void changePasswordSubmit_NewPassWordNotMatchConfirmNewPassword() throws Exception {
        mockMvc.perform(postChangePasswordForm("User@123", "Admin@123", "abc"))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(model().attributeHasFieldErrors("changePasswordForm", "confirmNewPassword"));
    }

    @Test
    public void changePasswordSubmit_Success() throws Exception {
        mockMvc.perform(postChangePasswordForm("User@123", "Admin@123", "Admin@123"))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(model().hasNoErrors());
    }

    private MockHttpServletRequestBuilder postChangePasswordForm(String currentPassword, String newPassword,
                                                                 String confirmNewPassword) {
        Principal principal = () -> TEST_USERNAME;

        return post("/user/change-password")
            .accept(MediaType.MULTIPART_FORM_DATA)
            .param("currentPassword", currentPassword)
            .param("newPassword", newPassword)
            .param("confirmNewPassword", confirmNewPassword)
            .with(mockHttpServletRequest -> {
                mockHttpServletRequest.setUserPrincipal(principal);
                return mockHttpServletRequest;
            });
    }

}
