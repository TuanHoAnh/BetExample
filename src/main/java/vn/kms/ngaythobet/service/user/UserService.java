/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.service.user;

import antlr.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.user.Language;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.user.UserRole;
import vn.kms.ngaythobet.domain.util.DataGenerator;
import vn.kms.ngaythobet.web.util.EmailValidate;
import vn.kms.ngaythobet.web.util.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    private static final Long SECOND_DECREASE = 10 * 24 * 60 * 60L;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailingService mailingService;

    @Value("${app.activation-expire-seconds}")
    private int activationExpireSeconds;

    @Value("${app.reset-expire-seconds}")
    private int resetPasswordExpireSeconds;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder, MailingService mailingService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailingService = mailingService;
    }

    public List<Language> getAllowedLanguages(Locale locale) {
        List<Language> languages = new ArrayList<>();

        languages.add(new Language("vi", Locale.forLanguageTag("vi").getDisplayLanguage(locale)));
        languages.add(new Language("en", Locale.forLanguageTag("en").getDisplayLanguage(locale)));

        return languages;
    }

    public void registerUser(User rawUser) {
        String encodedPassword = passwordEncoder.encode(rawUser.getPassword());

        User user = new User();
        BeanUtils.copyProperties(rawUser, user);

        user.setId(null);
        user.setPassword(encodedPassword);
        user.setActivationKey(DataGenerator.generateTokenKey());
        user.setActivated(false);
        user.setRole(UserRole.USER);

        user.setUsername(user.getUsername().toLowerCase(new Locale(user.getLanguageTag())));
        user.setEmail(user.getEmail().toLowerCase(new Locale(user.getLanguageTag())));

        userRepo.save(user);

        mailingService.sendActivationKeyAsync(user);
    }

    public void updateProfileUser(String username, String firstName, String lastName, String languageTag, String avatar) {
        userRepo.findOneByUsername(username).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setLanguageTag(languageTag);
            user.setAvatar(avatar);

            userRepo.save(user);
        });
    }

    public ActivateUserStatus activateUser(String activationToken) {
        User user = userRepo.findOneByActivationKey(activationToken).orElse(null);

        if (user == null) {
            return ActivateUserStatus.TOKEN_NOT_FOUND;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        Long period = ChronoUnit.SECONDS.between(user.getCreatedAt(), currentTime);
        if (period <= activationExpireSeconds) {
            user.setActivated(true);
            user.setActivationKey(null);
            userRepo.save(user);

            return ActivateUserStatus.ACTIVATE_SUCCESS;
        }

        return ActivateUserStatus.EXPIRED;
    }

    /**
     * @return <code>null</code> if email isn't associated to existing user
     */
    public Pair<String, Boolean> initResetPassword(String email) {
        User user = userRepo.findOneByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        String resetKey = DataGenerator.generateTokenKey();
        Boolean isActived = user.isActivated() ;

        user.setResetKey(resetKey);
        user.setResetAt(LocalDateTime.now());
        userRepo.save(user);

        if(isActived) {
            mailingService.sendResetKeyAsync(user);
        }

        return new Pair<>(resetKey, isActived);
    }

    public boolean validateResetToken(String resetToken) {
        User user = userRepo.findOneByResetKey(resetToken).orElse(null);
        return (user != null) && (ChronoUnit.SECONDS.between(user.getResetAt(), LocalDateTime.now()) <= resetPasswordExpireSeconds);
    }

    public void resetPassword(String username, String newPassword) {
        userRepo.findOneByUsername(username).ifPresent(user -> {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepo.save(user);
        });
    }

    public void updateResetAt(String username) {
        userRepo.findOneByUsername(username).ifPresent(user -> {
            LocalDateTime dayInPast = user.getResetAt().minusSeconds(SECOND_DECREASE + resetPasswordExpireSeconds);
            user.setResetAt(dayInPast);

            userRepo.save(user);
        });
    }

    public ChangePasswordError validateChangePassword(String username, String currentPassword, String newPassword) {
        User user = userRepo.findOneByUsername(username).orElse(null);

        if (user != null) {
            String validPassword = user.getPassword();
            String validNewPassword = passwordEncoder.encode(newPassword);

            if (!passwordEncoder.matches(currentPassword, validPassword)) {
                return ChangePasswordError.INVALID_CURRENT_PASSWORD;
            } else if (passwordEncoder.matches(currentPassword, validNewPassword)) {
                return ChangePasswordError.SAME_CURRENT_PASSWORD;
            }
        }

        return null;
    }

    public User findOneByResetkey(String resetKey) {
        return userRepo.findOneByResetKey(resetKey).orElse(null);
    }

    public List<User> convertListUsernameToListUser(List<String> listUsername) {
        return listUsername.stream()
            .filter(s-> !EmailValidate.isValidEmail(s))
            .map(userRepo::findOneByUsername)
            .map(user -> user.orElse(null))
            .filter(Objects::nonNull)
            .collect(toList());
    }

    public List<User> findByUsernameKeyword(String keyword) {
        return userRepo.findByEmailIgnoreCaseContainingOrUsernameIgnoreCaseContaining(keyword, keyword).orElse(null);
    }

    public User findOne(Long id) {
        return userRepo.findOne(id);
    }

    public enum ActivateUserStatus {
        ACTIVATE_SUCCESS,
        TOKEN_NOT_FOUND,
        EXPIRED,
        ALREADY_ACTIVATED
    }

    public enum ChangePasswordError {
        INVALID_CURRENT_PASSWORD,
        SAME_CURRENT_PASSWORD;
    }

}
