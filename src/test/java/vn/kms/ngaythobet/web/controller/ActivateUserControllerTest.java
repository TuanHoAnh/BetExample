package vn.kms.ngaythobet.web.controller;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.LocaleResolver;
import vn.kms.ngaythobet.BaseControllerTest;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.service.user.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UserController.class)
public class ActivateUserControllerTest extends BaseControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private LocaleResolver localeResolver;

    @Test
    public void getActivateTokenNotFound_Null() throws Exception {
        this.mockMvc.perform(get("/user/activate")
            .param("key", " ")
            .characterEncoding("UTF-8")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user/activate"));

    }
}
