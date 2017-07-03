/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.user;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.service.user.UserService;
import vn.kms.ngaythobet.service.user.UserService.ChangePasswordError;
import vn.kms.ngaythobet.domain.util.DataGenerator;
import vn.kms.ngaythobet.web.util.Pair;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest extends BaseTest {
    private static final String TEST_USERNAME = "test";
    private static final Long SECOND_DECREASE = 10 * 24 * 60 * 60L;
    @Value("${app.reset-expire-seconds}")
    private int resetPasswordExpireSeconds;

    @Value("${app.activation-expire-seconds}")
    private Long activationExpireSeconds;

    @Value("${ngaythobet.user-avatar}")
    private String userDefaultAvatar;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @Override
    protected void doStartUp() {
        MailingService mailService = mock(MailingService.class);
        when(mailService.sendEmailAsync(anyString(), anyString(), anyString()))
            .thenReturn(new AsyncResult<>(true));
        userService = new UserService(userRepo, passwordEncoder, mailService);
    }

    @Override
    protected void doTearDown() {
        userRepo
            .findOneByUsername(TEST_USERNAME)
            .ifPresent(userRepo::delete);
    }

    @Test
    public void testRegisterUser() {
        User rawUser = mockRawUser();

        userService.registerUser(rawUser);
        User user = userRepo.findOneByUsername(rawUser.getUsername()).get();

        assertThat(passwordEncoder.matches(rawUser.getPassword(), user.getPassword())).isTrue();
    }

    @Test
    public void testUpdateProfileUser_FirstName() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String updateFirstName = "updatedFirst";
        userService.updateProfileUser(user.getUsername(), updateFirstName, user.getLastName(), user.getLanguageTag(), user.getAvatar());

        User updateUser = userRepo.findOneByUsername(user.getUsername()).get();

        assertThat(updateFirstName).isEqualTo(updateUser.getFirstName());
    }

    @Test
    public void testUpdateProfileUser_LastName() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String updateLastName = "updatedLast";
        userService.updateProfileUser(user.getUsername(), user.getFirstName(), updateLastName, user.getLanguageTag(), user.getAvatar());

        User updateUser = userRepo.findOneByUsername(user.getUsername()).get();

        assertThat(updateLastName).isEqualTo(updateUser.getLastName());
    }

    @Test
    public void testUpdateProfileUser_LanguageTag() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String updateLanguageTag = "en";
        userService.updateProfileUser(user.getUsername(), user.getFirstName(), user.getLastName(), updateLanguageTag, user.getAvatar());

        User updateUser = userRepo.findOneByUsername(user.getUsername()).get();

        assertThat(updateLanguageTag).isEqualTo(updateUser.getLanguageTag());
    }

    @Test
    public void testUpdateProfileUser_Avatar() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String updateAvatar = "newAvatarURL";
        userService.updateProfileUser(user.getUsername(), user.getFirstName(), user.getLastName(), user.getLanguageTag(), updateAvatar);

        User updateUser = userRepo.findOneByUsername(user.getUsername()).get();

        assertThat(updateAvatar).isEqualTo(updateUser.getAvatar());
    }

    @Test
    public void testUpdateProfileUser_All() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String updateFirstName = "updatedTest";
        String updateLastName = "updatedLast";
        String updateLanguageTag = "vn";

        userService.updateProfileUser(user.getUsername(), updateFirstName, updateLastName, updateLanguageTag, user.getAvatar());
        User updateUser = userRepo.findOneByUsername(user.getUsername()).get();

        assertThat(updateFirstName).isEqualTo(updateUser.getFirstName());
        assertThat(updateLastName).isEqualTo(updateUser.getLastName());
        assertThat(updateLanguageTag).isEqualTo(updateUser.getLanguageTag());
    }

    @Test
    public void testValidateChangePassword_InvalidCurrentPassword() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String currentPassword = "Congle@123";
        String newPassword = "Admin@123";

        ChangePasswordError error = userService.validateChangePassword(TEST_USERNAME,
            currentPassword, newPassword);

        assertThat(error).isEqualTo(ChangePasswordError.INVALID_CURRENT_PASSWORD);
    }

    @Test
    public void testValidateChangePassword_Success() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String currentPassword = "Test@123";
        String newPassword = "Admin@123";

        ChangePasswordError error = userService.validateChangePassword(TEST_USERNAME,
            currentPassword, newPassword);

        assertThat(error).isNull();
    }

    @Test
    public void testValidateChangePassword_SameCurrentPassword() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String currentPassword = "Test@123";
        String newPassword = "Test@123";

        ChangePasswordError error = userService.validateChangePassword(TEST_USERNAME,
            currentPassword, newPassword);

        assertThat(error).isEqualTo(ChangePasswordError.SAME_CURRENT_PASSWORD);
    }

    @Test
    public void testInitResetPassword_Success() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String resetKey = userService.initResetPassword(user.getEmail()).getFirst();
        User savedUser = userRepo.findOneByUsername(user.getUsername()).get();
        assertThat(resetKey).isEqualTo(savedUser.getResetKey());
    }

    @Test
    public void testInitResetPassword_WrongEmail() {
        User user = mockActivatedUser();
        userRepo.save(user);

        Pair<String, Boolean> resetPassword = userService.initResetPassword("wrong_mail@gmail.com");

        assertThat(resetPassword).isNull();
    }

    @Test
    public void testValidateResetToken_Success() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String resetKey = userService.initResetPassword(user.getEmail()).getFirst();
        assertThat(userService.validateResetToken(resetKey)).isTrue();
    }

    @Test
    public void testValidateResetToken_Wrong_Token() {
        User user = mockActivatedUser();
        userRepo.save(user);

        String resetKey = userService.initResetPassword(user.getEmail()).getFirst();
        assertThat(userService.validateResetToken(resetKey + "1")).isFalse();
    }

    @Test
    public void testFindOneByResetkey_Success() {
        User user = mockActivatedUser();
        user.setResetKey(DataGenerator.generateTokenKey());
        userRepo.save(user);

        User savedUser = userService.findOneByResetkey(user.getResetKey());
        assertThat(savedUser.getResetKey()).isEqualTo(user.getResetKey());
    }

    @Test
    public void testFindOneByResetkey_FoundNoUser() {
        User user = mockActivatedUser();
        user.setResetKey(DataGenerator.generateTokenKey());
        userRepo.save(user);

        User savedUser = userService.findOneByResetkey("wrong_token");
        assertThat(savedUser).isNull();
    }

    @Test
    public void testActivateUser_ActivationToken_Activate_Success() {
        User user = mockRawUser();
        user.setRole(UserRole.USER);
        user.setActivated(false);
        String activationToken = DataGenerator.generateTokenKey();
        user.setActivationKey(activationToken);
        userRepo.save(user);

        userService.activateUser(activationToken);
        MatcherAssert.assertThat("The system activated user", userRepo
                .findOneByUsername(TEST_USERNAME)
                .get().isActivated()
            , is(equalTo(true)));


    }

    @Test
    public void testActivateUser_ActivationToken_Token_Not_Found() {
        User user = mockRawUser();
        user.setRole(UserRole.USER);
        String activationToken = DataGenerator.generateTokenKey();
        user.setActivationKey(activationToken);
        userRepo.save(user);

        MatcherAssert.assertThat("The system did not activate user because token not found", userService.activateUser("blalba"), is(equalTo(UserService.ActivateUserStatus.TOKEN_NOT_FOUND)));

    }

    @Test
    public void testActivateUser_ActivationToken_Token_Expired() {
        User user = mockRawUser();
        user.setRole(UserRole.USER);

        String activationToken = DataGenerator.generateTokenKey();
        user.setActivationKey(activationToken);
        user.setActivated(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepo.save(user);


        user.setCreatedAt(user.getCreatedAt().minusSeconds(activationExpireSeconds + 10000));
        userRepo.save(user);


        MatcherAssert.assertThat("The system should activated user", userService.activateUser(activationToken), is(equalTo(UserService.ActivateUserStatus.EXPIRED)));

    }

    private User mockRawUser() {
        User user = new User();

        user.setUsername(TEST_USERNAME);
        user.setPassword("Test@123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@test.local");
        user.setLanguageTag("en");

        return user;
    }

    private User mockActivatedUser() {
        User user = mockRawUser();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER);
        user.setActivated(true);
        user.setAvatar(userDefaultAvatar);

        return user;
    }

    @Test
    public void testConvertListUsernameToListUser() {

        List<String> listUsername = Arrays.asList("admin");
        List<User> listUser = userService.convertListUsernameToListUser(listUsername);

        MatcherAssert.assertThat("The result should be 1", listUser.size(), is(equalTo(1)));

    }

}
