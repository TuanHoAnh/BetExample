/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.user;

public enum UserRole {
    ADMIN,
    USER;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
