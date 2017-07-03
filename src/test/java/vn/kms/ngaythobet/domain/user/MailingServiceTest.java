package vn.kms.ngaythobet.domain.user;

/**
 * Created by vuhtran on 4/7/2017.
 */
/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.exceptions.TemplateInputException;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.common.MailingService;
import vn.kms.ngaythobet.domain.util.EmailTemplateWrapper;
import vn.kms.ngaythobet.service.user.UserService;

import java.util.concurrent.Future;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MailingServiceTest extends BaseTest {
    private static final String TEST_USERNAME = "test";
    private static final String ACTIVATION_KEY = "activationKey";
    private static final String HOST_DOMAIN = "baseUrl";
    private static final String LANGUAGE = "lang";

    @Value("${app.mail-sender}")
    private String sender;

    @Value("${app.host-domain}")
    private String hostDomain;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailingService mailingService;

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
    public void testSendEmailTo_RawUser_EmailTemplate_Success() throws Exception {
        User user = mockRawUser();
        userRepo.save(user);

        String receiverEmail = user.getEmail();
        String subject = "email.activation.title";
        String htmlTemplate = "mailing/activation";
        EmailTemplateWrapper emailTemplate = new EmailTemplateWrapper();
        emailTemplate.setHtmlTemplate(htmlTemplate)
            .setSubject(subject)
            .setVariable(TEST_USERNAME, user.getFirstName())
            .setVariable(HOST_DOMAIN, hostDomain)
            .setVariable(ACTIVATION_KEY, user.getActivationKey())
            .setVariable(LANGUAGE, user.getLanguageTag())
            .setReceiver(receiverEmail);

        Future<Boolean> result = mailingService.sendEmailUsingTemplateAsync(emailTemplate);


        final Boolean isSentEmail = result.get();

        MatcherAssert.assertThat("The system should complete sending email", isSentEmail, is(equalTo(true)));


    }

    @Test(expected = NullPointerException.class)
    public void testSendEmailTo_RawUser_EmailTemplate_NoLanguageTag_Fail() throws Exception {
        User user = mockRawUser();
        user.setLanguageTag(null);
        userRepo.save(user);

        String receiverEmail = user.getEmail();
        String subject = "email.activation.title";
        String htmlTemplate = "mailing/activation";
        EmailTemplateWrapper emailTemplate = new EmailTemplateWrapper();
        emailTemplate.setHtmlTemplate(htmlTemplate)
            .setSubject(subject)
            .setVariable(TEST_USERNAME, user.getFirstName())
            .setVariable(HOST_DOMAIN, hostDomain)
            .setVariable(ACTIVATION_KEY, user.getActivationKey())
            .setVariable(LANGUAGE, user.getLanguageTag())
            .setReceiver(receiverEmail);

        Future<Boolean> result = mailingService.sendEmailUsingTemplateAsync(emailTemplate);

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testSendEmailTo_RawUser_EmailTemplate__NoFirstName_Fail() throws Exception {
        User user = mockRawUser();
        user.setFirstName(null);
        userRepo.save(user);

        String receiverEmail = user.getEmail();
        String subject = "email.activation.title";
        String htmlTemplate = "mailing/activation";
        EmailTemplateWrapper emailTemplate = new EmailTemplateWrapper();
        emailTemplate.setHtmlTemplate(htmlTemplate)
            .setSubject(subject)
            .setVariable(TEST_USERNAME, user.getFirstName())
            .setVariable(HOST_DOMAIN, hostDomain)
            .setVariable(ACTIVATION_KEY, user.getActivationKey())
            .setVariable(LANGUAGE, user.getLanguageTag())
            .setReceiver(receiverEmail);

        Future<Boolean> result = mailingService.sendEmailUsingTemplateAsync(emailTemplate);

    }

    @Test(expected = TemplateInputException.class)
    public void testSendEmailTo_RawUser_EmailTemplate__WrongTemplate_Fail() throws Exception {
        User user = mockRawUser();
        userRepo.save(user);

        String receiverEmail = user.getEmail();
        String subject = "email.activation.title";
        String htmlTemplate = "mailing/dasdsa";
        EmailTemplateWrapper emailTemplate = new EmailTemplateWrapper();
        emailTemplate.setHtmlTemplate(htmlTemplate)
            .setSubject(subject)
            .setVariable(TEST_USERNAME, user.getFirstName())
            .setVariable(HOST_DOMAIN, hostDomain)
            .setVariable(ACTIVATION_KEY, user.getActivationKey())
            .setVariable(LANGUAGE, user.getLanguageTag())
            .setReceiver(receiverEmail);

        Future<Boolean> result = mailingService.sendEmailUsingTemplateAsync(emailTemplate);

    }

    @Test
    public void testSend_ActivationEmail_User_Success() throws Exception {
        User user = mockRawUser();
        userRepo.save(user);
        Future<Boolean> result = mailingService.sendActivationKeyAsync(user);

        MatcherAssert.assertThat("The system should complete sending email", result.get(), is(equalTo(true)));

    }


    @Test(expected = NullPointerException.class)
    public void testSend_ActivationEmail_User_NoLanguageTag_Fail() throws Exception {
        User user = mockRawUser();
        user.setLanguageTag(null);
        userRepo.save(user);
        Future<Boolean> result = mailingService.sendActivationKeyAsync(user);

    }

    @Test
    public void testSend_ActivationEmail_User_Already_Activated_Fail() throws Exception {
        User user = mockRawUser();
        user.setActivated(true);
        userRepo.save(user);
        Future<Boolean> result = mailingService.sendActivationKeyAsync(user);

        MatcherAssert.assertThat("The system should not complete sending email because user is already activated", result.get(), is(equalTo(false)));

    }


    private User mockRawUser() {
        User user = new User();

        user.setUsername(TEST_USERNAME);
        user.setPassword("Test@123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@test.local");
        user.setLanguageTag("en");
        user.setRole(UserRole.USER);
        user.setActivated(false);

        return user;
    }

}

