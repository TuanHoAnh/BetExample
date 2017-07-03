/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.kms.ngaythobet.domain.user.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Locale;

import static java.util.Collections.singletonList;

public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("exception.auth.user-not-activated");
        }
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActivated();
    }

    public String getFullName() {
        return user.getFirstName() + " " + user.getLastName();
    }

    public LocalDate getRegisteredDate() {
        return user.getCreatedAt().toLocalDate();
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(user.getLanguageTag());
    }

    public User getUser() {
        return user;
    }
}
