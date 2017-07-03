/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.controller;


import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.LocaleResolver;
import vn.kms.ngaythobet.BaseControllerTest;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.user.Language;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.service.user.UserService;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class ResetPasswordControllerTest extends BaseControllerTest{
    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private LocaleResolver localeResolver;

    @Override
    protected void doStartUp() {
        when(userService.getAllowedLanguages(any()))
            .thenReturn(Arrays.asList(new Language("vi", "Vietnamese"), new Language("en", "English")));
        when(userService.findOneByResetkey(any())).thenReturn(new User());
    }
    @Test
    public void resetPasswordFormTest_Valid() throws Exception{
        this.mockMvc.perform(get("/user/reset-password")
            .accept(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(view().name("user/reset-password"));
    }
    @Test
    public void resetPasswordSubmitTest_ValidParam() throws Exception{
        this.mockMvc.perform(post("/user/reset-password")
            .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("resetToken", "2c3ed720-4bcc-47a6-b7c6-a27cfe0c536a")
            .param("newPassword", "Admin@123")
            .param("confirmPassword", "Admin@123"))
            .andExpect(status().is(302))
            .andExpect(model().errorCount(0))
            .andExpect(redirectedUrl("/signin"));
    }
    @Test
    public void resetPasswordSubmitTest_EmptyNewPassword() throws Exception{
        this.mockMvc.perform(post("/user/reset-password")
            .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("resetToken", "2c3ed720-4bcc-47a6-b7c6-a27cfe0c536a")
            .param("newPassword", "")
            .param("confirmPassword", ""))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(2))
            .andExpect(view().name("user/reset-password"));
    }
    @Test
    public void resetPasswordSubmitTest_EmptyConfirmPassword() throws Exception{
        this.mockMvc.perform(post("/user/reset-password")
            .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("resetToken", "2c3ed720-4bcc-47a6-b7c6-a27cfe0c536a")
            .param("newPassword", "Admin@123")
            .param("confirmPassword", ""))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(view().name("user/reset-password"));
    }
    @Test
    public void resetPasswordSubmitTest_WeakNewPassword() throws Exception{
        this.mockMvc.perform(post("/user/reset-password")
            .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("resetToken", "2c3ed720-4bcc-47a6-b7c6-a27cfe0c536a")
            .param("newPassword", "Admin")
            .param("confirmPassword", "Admin"))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(view().name("user/reset-password"));
    }

    @Test
    public void resetPasswordSubmitTest_NotMatchPassword() throws Exception{
        this.mockMvc.perform(post("/user/reset-password")
            .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
            .param("resetToken", "2c3ed720-4bcc-47a6-b7c6-a27cfe0c536a")
            .param("newPassword", "Admin@123")
            .param("confirmPassword", "Admin@456"))
            .andExpect(status().is(200))
            .andExpect(model().errorCount(1))
            .andExpect(view().name("user/reset-password"));
    }





}
