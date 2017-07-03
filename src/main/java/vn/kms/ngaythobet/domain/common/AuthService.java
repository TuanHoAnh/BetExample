/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.infras.security.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> getLoginUser() {
        String login = getLogin();
        if (login == null) {
            return Optional.empty();
        }

        return userRepo.findOneByUsername(login);
    }

    public String getLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        String username = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                username = userDetails.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();
            }
        }

        return username;
    }

    public UserRole getRole() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String authority = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                authority = userDetails.getAuthorities().toArray()[0].toString();
            } else if (authentication.getPrincipal() instanceof String) {
                authority = authentication.getAuthorities().toArray()[0].toString();
            }
        }

        return UserRole.valueOf(authority);
    }

    private String generateUserNameForSSOUser(String email) {

        String userNameGenerated = email.replaceAll("@(.+)$", "").replaceAll("[^a-zA-Z]", "");

        User user = userRepo.findOneByUsername(userNameGenerated).orElse(null);
        int min = 100;
        int max = 999;
        if (Objects.isNull(user)) {
            return userNameGenerated;
        }

        int suffix = new Random().nextInt((max - min) + 1) + min;
        return userNameGenerated + suffix;

    }

    public void signInBySSO(OAuth2Authentication oAuth2Authentication) {
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        Map<String, String> details = (Map<String, String>) userAuthentication.getDetails();
        String emailSSO = details.get("email");

        if (StringUtils.isEmpty(emailSSO)) {
            userAuthentication.setAuthenticated(false);
            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            return;
        }
        String firstName = details.getOrDefault("given_name", details.get("first_name"));
        String lastName = details.getOrDefault("family_name", details.get("last_name"));
        String locale = details.getOrDefault("locale", "en");
        if (locale.contains("vi") || locale.contains("vn")) {
            locale = "vi";
        }
        User socialUser = createUserFromSSO(emailSSO, firstName, lastName, locale);
        userRepo.save(socialUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(socialUser);

        Authentication authenticationSSO = new UsernamePasswordAuthenticationToken(userDetails, null,
            AuthorityUtils.createAuthorityList(socialUser.getRole().getAuthority()));
        SecurityContextHolder.getContext().setAuthentication(authenticationSSO);
    }

    private User createUserFromSSO(String emailSSO, String firstName, String lastName, String locale) {

        User userSSO = userRepo.findOneByEmail(emailSSO).orElse(null);
        if (Objects.isNull(userSSO)) {
            return createNewUserSSO(emailSSO, firstName, lastName, locale);
        }
        updateUserSSO(userSSO, firstName, lastName);
        return userSSO;
    }

    private User createNewUserSSO(String emailSSO, String firstName, String lastName, String locale) {
        User newUser = new User();
        newUser.setActivated(true);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setUsername(generateUserNameForSSOUser(emailSSO));
        newUser.setAuthProvider("SSO");
        newUser.setRole(UserRole.USER);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setEmail(emailSSO);
        newUser.setLanguageTag(locale);
        return newUser;
    }

    private void updateUserSSO(User user, String newFirstName, String newLastName) {
        user.setLastName(newLastName);
        user.setFirstName(newFirstName);
        user.setActivated(true);
    }
}
