/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.web.util.DataResponse;
import vn.kms.ngaythobet.web.util.Pair;

@RestController
@RequestMapping("/api/users")
public class UserApi {
    @Autowired
    private UserService userService;

    @PostMapping("/reset-password")
    public DataResponse<Pair<String, Boolean>> resetPassword(@RequestBody String email) {
        Pair<String, Boolean> resetPassword = userService.initResetPassword(email);

        return new DataResponse<>(resetPassword);
    }
}
