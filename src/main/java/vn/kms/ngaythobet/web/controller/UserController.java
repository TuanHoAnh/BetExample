/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.service.file.FileService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.form.*;
import vn.kms.ngaythobet.web.util.DataResponse;
import vn.kms.ngaythobet.web.util.Pair;
import vn.kms.ngaythobet.web.util.UserSearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final String USER_RESET_PASS = "user/reset-password";
    private static final String USER_PROFILE = "user/profile";
    private static final String USER_PROFILE_CONTENT = "user/profile :: update-profile-content";

    @Value("${ngaythobet.user-avatar}")
    private String userDefaultAvatar;

    @Autowired
    private UserService userService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private AuthService authService;
    @Autowired
    private FileService fileService;

    @GetMapping("/register")
    public String registerForm(RegistrationForm registrationForm) {
        return "user/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid RegistrationForm registrationForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        User rawUser = new User();
        BeanUtils.copyProperties(registrationForm, rawUser);
        rawUser.setAvatar(userDefaultAvatar);
        userService.registerUser(rawUser);

        return "redirect:/user/register?success";
    }

    @GetMapping("/activate")
    public ModelAndView activateUser(@RequestParam String key) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("status", userService.activateUser(key));

        return modelAndView;
    }

    @PostMapping("/reset-password-init")
    @ResponseBody
    public DataResponse<Pair<String, Boolean>> initResetPassword(@RequestBody InitResetPasswordForm initResetPasswordForm) {
        Pair<String, Boolean> resetPassword = userService.initResetPassword(initResetPasswordForm.getEmail());

        return new DataResponse<>(resetPassword);
    }

    @GetMapping("/reset-password")
    public Object resetPasswordForm(String key, ResetPasswordForm resetPasswordForm) {
        ModelAndView modelAndView = new ModelAndView(USER_RESET_PASS);
        boolean invalid = !userService.validateResetToken(key);
        modelAndView.addObject("invalid", invalid);

        resetPasswordForm.setResetToken(key);

        return modelAndView;
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@Valid ResetPasswordForm resetPasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return USER_RESET_PASS;
        }

        User user = userService.findOneByResetkey(resetPasswordForm.getResetToken());

        if (user == null) {
            return USER_RESET_PASS;
        }

        userService.resetPassword(user.getUsername(), resetPasswordForm.getNewPassword());
        userService.updateResetAt(user.getUsername());

        return "redirect:/signin";
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {

        authService.getLoginUser().ifPresent(user -> {
            model.addAttribute("userProfileForm", new UserProfileForm());
            model.addAttribute("changePasswordForm", new ChangePasswordForm());
            if ("SSO".equalsIgnoreCase(user.getAuthProvider())) {
                request.setAttribute("SSO", "SSO");
            }
        });

        return USER_PROFILE;
    }

    @PostMapping("/update-profile")
    public String updateProfileSubmit(@Valid UserProfileForm userProfileForm, BindingResult bindingResult,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (bindingResult.hasErrors()) {
            return USER_PROFILE_CONTENT;
        }

        if (Objects.nonNull(userProfileForm.getAvatarFile())) {
            String urlAvatar = fileService.saveUserAvatar(userProfileForm.getAvatarFile(), String.valueOf(userProfileForm.getId()));
            userProfileForm.setAvatar(urlAvatar);
        }

        if (StringUtils.isEmpty(userProfileForm.getAvatar())) {
            userProfileForm.setAvatar(userDefaultAvatar);
        }

        userService.updateProfileUser(
            authService.getLogin(),
            userProfileForm.getFirstName(),
            userProfileForm.getLastName(),
            userProfileForm.getLanguageTag(),
            userProfileForm.getAvatar()
        );

        localeResolver.setLocale(request, response, new Locale(userProfileForm.getLanguageTag()));

        return USER_PROFILE_CONTENT;
    }

    @GetMapping("/update-profile")
    public String getUserProfile(@Valid Model model) {
        authService.getLoginUser().ifPresent(user -> model.addAttribute("userProfileForm", UserProfileForm.from(user)));

        return USER_PROFILE_CONTENT;
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(@Valid @ModelAttribute ChangePasswordForm changePasswordForm,
                                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/profile :: change-password-content";
        }
        userService.resetPassword(authService.getLogin(), changePasswordForm.getNewPassword());
        return "user/profile :: change-password-content";
    }

    @GetMapping("/signin-sso")
    public String ssoAuthorize(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication && authentication.isAuthenticated()) {
            authService.signInBySSO((OAuth2Authentication) authentication);
        }

        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return "redirect:/";
        }
        request.setAttribute("errorSSO", "error");
        request.getSession().invalidate();
        return "signin";

    }

    @GetMapping("/search-user")
    @ResponseBody
    public DataResponse<List<UserSearchResult>> bettingGroupFormSearchUser(@RequestParam String search) {


        List<User> usernames = userService.findByUsernameKeyword(search).stream()
            .filter(User::isActivated)
            .collect(Collectors.toList());

        List<UserSearchResult> userSearchResults = new ArrayList<>();
        for (User user : usernames) {
            userSearchResults.add(new UserSearchResult(user.getUsername(), user.getUsername() + " <" + user.getEmail() + ">"));
        }

        return new DataResponse<>(userSearchResults);
    }

}
