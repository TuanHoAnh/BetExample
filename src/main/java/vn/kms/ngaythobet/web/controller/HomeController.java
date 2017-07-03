/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.user.UserRole;


@Controller
public class HomeController {
    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signin")
    public String signin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "signin";
    }

    @GetMapping("/user-guide")
    public String userGuide(Model model) {
        model.addAttribute("isAdmin", false);

        authService.getLoginUser()
            .filter(user -> user.getRole().equals(UserRole.ADMIN))
            .ifPresent(user -> model.addAttribute("isAdmin", true));
        return "user-guide";
    }

}
