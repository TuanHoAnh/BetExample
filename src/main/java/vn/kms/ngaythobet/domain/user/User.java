/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Data
@EqualsAndHashCode(of = "username")
@NoArgsConstructor
public class User extends BaseEntity {
    public static final String SYSTEM_USER = "system";

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String languageTag;

    private boolean activated;
    private String activationKey;

    private String resetKey;
    private LocalDateTime resetAt;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String authProvider;

    private String avatar;

    @Transient
    public String getFullName(){
        return firstName + " " + lastName;
    }

}
