/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring4.SpringTemplateEngine;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingMatch;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.util.EmailTemplateWrapper;
import vn.kms.ngaythobet.domain.util.exception.EmailException;
import vn.kms.ngaythobet.web.util.DateTimeFormat;

import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
@Slf4j
public class MailingService {


    private static final String NAME_OF_USER = "name";
    private static final String ACTIVATION_KEY = "activationKey";
    private static final String RESET_KEY = "resetKey";
    private static final String HOST_DOMAIN = "baseUrl";
    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_ID = "groupId";
    private static final String GROUP_COMPETITION_KEY = "competitionAliasKey";
    private static final String MODERATOR_NAME = "moderator";
    private static final String BETTING_MATCH_NAME = "matchName";
    private static final String BETTING_AMOUNT = "betAmount";
    private static final String BETTING_BALANCE = "balance";
    private static final String EXPIRED_TIME = "expiredTime";
    private static final String COMMENT = "comment";
    private static final String LINK_BETTING_MATCH = "linkBettingMatch";


    private JavaMailSender javaMailSender;

    private SpringTemplateEngine templateEngine;

    private UserRepository userRepo;

    private MessageSource messageSource;

    private CompetitionRepository competitionRepo;


    @Value("${app.mail-sender}")
    private String sender;


    @Value("${app.host-domain}")
    private String hostDomain;

    @Autowired
    public MailingService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, UserRepository userRepo,
                          MessageSource messageSource, CompetitionRepository competitionRepo) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.userRepo = userRepo;
        this.messageSource = messageSource;
        this.competitionRepo = competitionRepo;
    }


    @Async
    public Future<Boolean> sendEmailAsync(String subject, String content, String... to) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        boolean result;
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "utf-8");
            message.setFrom(sender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, true);
            javaMailSender.send(mimeMessage);
            result = true;
        } catch (Exception ex) {
            result = false;
            log.error("E-mail could not be sent to user " + to, ex);
        }

        return new AsyncResult<>(result);
    }


    @Async
    public Future<Boolean> sendEmailUsingTemplateAsync(EmailTemplateWrapper emailTemplate) {
        String recipient = emailTemplate.getReceiver();

        Locale locale = Locale.getDefault();

        Optional<User> value = userRepo.findOneByEmail(recipient);

        User user;
        if (value.isPresent()) {
            user = value.get();
            recipient = user.getEmail();
            locale = new Locale(user.getLanguageTag());
        }

        emailTemplate.setLocale(locale)
            .setMessageSource(messageSource)
            .setTemplateEngine(templateEngine).make();


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        boolean result;
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "utf-8");
            message.setFrom(sender);
            message.setTo(recipient);
            message.setSubject(emailTemplate.getSubject());
            message.setText(emailTemplate.getHtmlContent(), true);
            javaMailSender.send(mimeMessage);
            result = true;
        } catch (Exception ex) {
            result = false;
            log.error("E-mail could not be sent to user " + recipient, ex);
        }

        return new AsyncResult<>(result);
    }

    @Async
    public Future<Boolean> sendActivationKeyAsync(User user) {
        if (user.isActivated()) {
            return new AsyncResult<>(false);
        }
        try {
            EmailTemplateWrapper emailTemplate = new EmailTemplateWrapper();
            emailTemplate.setHtmlTemplate("mailing/activation")
                .setSubject("email.activation.title")
                .setVariable(NAME_OF_USER, user.getFirstName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setVariable(ACTIVATION_KEY, user.getActivationKey())
                .setReceiver(user.getEmail());

            sendEmailUsingTemplateAsync(emailTemplate);


        } catch (EmailException e) {
            log.error("E-mail could not be sent to user has email " + user.getEmail(), e);
            return new AsyncResult<>(false);


        }
        return new AsyncResult<>(true);
    }

    @Async
    public void sendResetKeyAsync(User user) {
        try {
            String receiver = user.getEmail();
            EmailTemplateWrapper emailTemplateWrapper = new EmailTemplateWrapper();
            emailTemplateWrapper.setHtmlTemplate("mailing/reset-password")
                .setSubject("email.reset.mail.title")
                .setVariable(NAME_OF_USER, user.getFirstName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setVariable(RESET_KEY, user.getResetKey())
                .setReceiver(receiver);

            sendEmailUsingTemplateAsync(emailTemplateWrapper);

        } catch (EmailException e) {
            log.warn("E-mail could not be sent to user has email " + user.getEmail(), e);
        }
    }

    @Async
    public void sendCreateBettingGroupAsync(BettingGroup bettingGroup) {
        try {
            EmailTemplateWrapper emailTemplateWrapper = new EmailTemplateWrapper();
            emailTemplateWrapper.setHtmlTemplate("mailing/create-betting-group")
                .setSubject("createBettingGroup.email.title")
                .setVariable(NAME_OF_USER, bettingGroup.getModerator().getFirstName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setVariable(GROUP_COMPETITION_KEY, bettingGroup.getCompetition().getAliasKey())
                .setVariable(GROUP_ID, bettingGroup.getId())
                .setVariable(GROUP_NAME, bettingGroup.getName())
                .setReceiver(bettingGroup.getModerator().getEmail());

            sendEmailUsingTemplateAsync(emailTemplateWrapper);

        } catch (EmailException e) {
            log.warn("E-mail could not be sent to user has email " + bettingGroup.getModerator().getEmail(), e);
        }
    }

    @Async
    public void sendNotiApproveCreatingBettingGroup(User user, BettingGroup bettingGroup) {
        try {
            EmailTemplateWrapper emailTemplateWrapper = new EmailTemplateWrapper();
            emailTemplateWrapper.setHtmlTemplate("mailing/inform-approve-request")
                .setSubject("email.bettinggroup.request.title")
                .setVariable(NAME_OF_USER, user.getFirstName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setVariable(GROUP_NAME, bettingGroup.getName())
                .setVariable(GROUP_COMPETITION_KEY, bettingGroup.getCompetition().getAliasKey())
                .setVariable(GROUP_ID, bettingGroup.getId())
                .setReceiver(user.getEmail());

            sendEmailUsingTemplateAsync(emailTemplateWrapper);

        } catch (EmailException e) {
            log.warn("E-mail could not be sent to user has email " + user.getEmail(), e);
        }
    }

    @Async
    public void sendNotiRejectCreatingBettingGroup(User user, String groupName) {
        try {
            EmailTemplateWrapper emailTemplateWrapper = new EmailTemplateWrapper();
            emailTemplateWrapper.setHtmlTemplate("mailing/inform-reject-request")
                .setSubject("email.bettinggroup.request.title")
                .setVariable(NAME_OF_USER, user.getFirstName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setVariable(GROUP_NAME, groupName)
                .setReceiver(user.getEmail());

            sendEmailUsingTemplateAsync(emailTemplateWrapper);

        } catch (EmailException e) {
            log.warn("E-mail could not be sent to user has email " + user.getEmail(), e);
        }
    }

    @Async
    public void sendNotificationToPlayerInBettingGroupAsync(BettingGroup bettingGroup, User user) {
        try {
            EmailTemplateWrapper emailTemplateWrapper = new EmailTemplateWrapper();
            emailTemplateWrapper.setHtmlTemplate("mailing/publish-betting-group")
                .setSubject("email.bettinggroup.new.mail.title")
                .setVariable(NAME_OF_USER, user.getFirstName())
                .setVariable(GROUP_NAME, bettingGroup.getName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setVariable(GROUP_COMPETITION_KEY, bettingGroup.getCompetition().getAliasKey())
                .setVariable(GROUP_ID, bettingGroup.getId())
                .setReceiver(user.getEmail());

            sendEmailUsingTemplateAsync(emailTemplateWrapper);

        } catch (EmailException e) {
            log.warn("E-mail could not be sent to user has email " + user.getEmail(), e);
        }
    }

    @Async
    public void sendNotificationToEmailInBettingGroupAsync(BettingGroup bettingGroup, String email) {
        try {
            EmailTemplateWrapper emailTemplateWrapper = new EmailTemplateWrapper();
            emailTemplateWrapper.setHtmlTemplate("mailing/invite-player")
                .setSubject("email.bettinggroup.new.mail.title")
                .setVariable(NAME_OF_USER, " Guest")
                .setVariable(MODERATOR_NAME, bettingGroup.getModerator().getFirstName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setReceiver(email);

            sendEmailUsingTemplateAsync(emailTemplateWrapper);

        } catch (EmailException e) {
            log.warn("E-mail could not be sent to user has email " + email, e);
        }
    }

    @Async
    public void sendNotificationUpdateBettingMatchToUser(BettingMatch bettingMatch, User user) {
        try {

            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(DateTimeFormat.DATE.getFormat());
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern(DateTimeFormat.TIME.getFormat());
            String formatDate = bettingMatch.getExpiryTime().format(formatterDate);
            String formatTime = bettingMatch.getExpiryTime().format(formatterTime);

            String linkBettingMatch = buildLinkBettingMatch(bettingMatch);
            EmailTemplateWrapper emailTemplateWrapper = new EmailTemplateWrapper();
            emailTemplateWrapper.setHtmlTemplate("mailing/update-betting-match")
                .setSubject("email.betting.match.new.mail.title")
                .setVariable(NAME_OF_USER, user.getFirstName())
                .setVariable(BETTING_MATCH_NAME, bettingMatch.getMatch().getCompetitor1().getName() + " vs " + bettingMatch.getMatch().getCompetitor2().getName())
                .setVariable(HOST_DOMAIN, hostDomain)
                .setVariable(LINK_BETTING_MATCH, linkBettingMatch)
                .setVariable(BETTING_AMOUNT, bettingMatch.getBettingAmount())
                .setVariable(BETTING_BALANCE, bettingMatch.getBalance1() + ": " + bettingMatch.getBalance2())
                .setVariable(EXPIRED_TIME, formatDate + " " + formatTime)
                .setVariable(COMMENT, bettingMatch.getComment())
                .setReceiver(user.getEmail());

            sendEmailUsingTemplateAsync(emailTemplateWrapper);

        } catch (EmailException e) {
            log.warn("E-mail could not be sent to user has email " + user.getEmail(), e);
        }
    }

    @Async
    public void sendNotificationToAllEmailInBettingGroupAsync(BettingGroup bettingGroup, List<String> emails) {
        try {

            for (String email : emails) {
                sendNotificationToEmailInBettingGroupAsync(bettingGroup, email);
            }
        } catch (EmailException e) {
            log.warn("E-mail could not be sent to non-user has email " + emails.toString(), e);
        }

    }


    @Async
    public void sendNotificationToBettingGroupPlayersAsync(BettingGroup bettingGroup, List<User> users) {

        for (User user : users) {
            try {
                sendNotificationToPlayerInBettingGroupAsync(bettingGroup, user);

            } catch (EmailException e) {
                log.warn("E-mail could not be sent to user has email " + user.getEmail(), e);
            }

        }

    }

    @Async
    public void sendNotificationUpdateBettingMatch(BettingMatch bettingMatch, List<User> users) {
        for (User user : users) {
            try {
                sendNotificationUpdateBettingMatchToUser(bettingMatch, user);
            } catch (EmailException e) {
                log.warn("E-mail could not be sent to user has email " + user.getEmail(), e);
            }
        }
    }


    @Transactional
    private String buildLinkBettingMatch(BettingMatch bettingMatch) {
        Long competitionId = bettingMatch.getBettingGroup().getCompetition().getId();
        String competitionAliasKey = competitionRepo.findOne(competitionId).getAliasKey();
        Long bettingGroupId = bettingMatch.getBettingGroup().getId();
        return String.format("#/competitions/%s/bettings/%s/matches/%s", competitionAliasKey, bettingGroupId, bettingMatch.getId());
    }
}
