/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation.bettinggroup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import org.springframework.util.StringUtils;
import vn.kms.ngaythobet.domain.betting.BettingGroup;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepository;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.domain.user.UserRepository;
import vn.kms.ngaythobet.web.util.EmailValidate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class AddNewPlayerValidator implements ConstraintValidator<AddNewPlayer, Object> {

    private static final String REGEX_USER = ",";
    private Class<? extends BaseEntity> entity;
    private String fieldName;
    private String groupIdField;
    private String playersField;
    private String message;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private BettingGroupRepository bettingGroupRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public void initialize(AddNewPlayer annotation) {
        entity = annotation.entity();
        fieldName = annotation.field();
        if (StringUtils.isEmpty(fieldName)) {
            fieldName = "";
        }
        message = annotation.message();
        playersField = annotation.players();
        groupIdField = annotation.currentGroupId();
    }

    @Override
    public boolean isValid(Object fieldValue, ConstraintValidatorContext context) {

        try {
            Long currentGroupId = Long.valueOf(FieldUtils.getFieldValue(fieldValue, groupIdField).toString());
            String listFormPlayers = FieldUtils.getFieldValue(fieldValue, playersField).toString();
            Optional<BettingGroup> bettingGroup = bettingGroupRepo.findBettingGroupById(currentGroupId);
            if (!bettingGroup.isPresent()) {
                return false;

            }
            List<String> currentGroupPlayers = new ArrayList<>(bettingGroup.get().getPlayers());

            List<String> distinctListPlayer = Arrays.stream(listFormPlayers
                .split(REGEX_USER))
                .distinct()
                .collect(Collectors.toList());

            distinctListPlayer.removeAll(currentGroupPlayers);

            List<String> distinctListPlayerUsernameOnly = distinctListPlayer.stream()
                .filter(s -> !EmailValidate.isValidEmail(s))
                .collect(Collectors.toList());


            String queryString = String.format("select %s from %s tb where tb.activated = true", fieldName, entity.getName());


            List<String> listExistingPlayers = (List<String>) em
                .createQuery(queryString)
                .getResultList();


            distinctListPlayerUsernameOnly.removeAll(listExistingPlayers);

            distinctListPlayerUsernameOnly = distinctListPlayerUsernameOnly
                .stream()
                .filter(s -> !EmailValidate.isValidEmail(s))
                .collect(Collectors.toList());


            if (!distinctListPlayerUsernameOnly.isEmpty()) {
                context.disableDefaultConstraintViolation();

                context.buildConstraintViolationWithTemplate(message + " " + String.join(",", distinctListPlayerUsernameOnly) + ".")
                    .addPropertyNode(playersField)
                    .addConstraintViolation();
                return false;
            }
            return true;
        } catch (IllegalAccessException e) {
            log.error("New player added invalid", e);
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("{updatebettinggroup.error.playersempty}")
                .addPropertyNode(playersField)
                .addConstraintViolation();
            return false;
        }
    }
}
