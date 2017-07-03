package vn.kms.ngaythobet.infras.validation.bettinggroup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.domain.util.exception.InvalidEmailUpdateBettingGroupException;
import vn.kms.ngaythobet.web.util.EmailValidate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ValidBettingGroupPlayerEmailExistingValidator implements ConstraintValidator<ValidBettingGroupPlayerEmailExisting, Object> {
    public static final String REGEX_USER = ",";


    private String currentGroupIdField;
    private String playersField;

    private String message;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BettingGroupRepository bettingGroupRepo;

    @Override
    public void initialize(ValidBettingGroupPlayerEmailExisting constraintAnnotation) {
        message = constraintAnnotation.message();
        currentGroupIdField = constraintAnnotation.currentGroupId();
        playersField = constraintAnnotation.players();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {


        try {
            Long currentGroupId = Long.valueOf(FieldUtils.getFieldValue(value, currentGroupIdField).toString());
            String listUpdatePlayer = FieldUtils.getFieldValue(value, playersField).toString();


            Optional<BettingGroup> bettingGroup = bettingGroupRepo.findBettingGroupById(currentGroupId);
            if (!bettingGroup.isPresent()){
                throw new InvalidEmailUpdateBettingGroupException("Cannot find the group id");
            }

            List<String> listStringExistingPlayer = bettingGroup.get().getPlayers();
            List<String> listStringExistingPlayerAsUsernameFormat = listStringExistingPlayer
                .stream()
                .filter(s -> !EmailValidate.isValidEmail(s))
                .collect(Collectors.toList());


            List<String> listUser = Arrays.asList(listUpdatePlayer.split(REGEX_USER));

            List<String> listEmailUser = listUser
                .stream()
                .filter(EmailValidate::isValidEmail)
                .collect(Collectors.toList());

            List<User> errorUsernameEmail = new ArrayList<>();

            for (String emailUser : listEmailUser) {
                Optional<User> user = userRepo.findOneByEmail(emailUser);
                if (user.isPresent()) {
                    errorUsernameEmail.add(user.get());

                }
            }


            if (errorUsernameEmail.isEmpty())
                return true;

            List<String> errorUsername = errorUsernameEmail
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
            if (Collections.disjoint(listStringExistingPlayerAsUsernameFormat, errorUsername))
                return true;


            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.join(",", convertUserListtoEmailList(errorUsernameEmail)) + " " + message)
                .addPropertyNode(playersField)
                .addConstraintViolation();
            return false;

        } catch (InvalidEmailUpdateBettingGroupException|IllegalAccessException ex) {
            log.error("Error at Update betting validator email - no betting group found", ex);
            return false;

        }


    }

    private List<String> convertUserListtoEmailList(List<User> users) {
        return users
            .stream()
            .map(User::getEmail)
            .collect(Collectors.toList());
    }
}



