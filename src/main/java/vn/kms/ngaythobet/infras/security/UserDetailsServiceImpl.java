/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kms.ngaythobet.domain.user.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Optional<vn.kms.ngaythobet.domain.user.User> dbUser = userRepo.findOneByUsername(username);

        return dbUser
            .map(UserDetailsImpl::new)
            .orElseThrow(() -> new UsernameNotFoundException("exception.auth.user-not-found"));
    }
}
