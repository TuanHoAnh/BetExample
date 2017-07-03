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
import vn.kms.ngaythobet.domain.util.exception.InvalidEmailUpdateBettingGroupException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ValidListUpdateGroupPlayerValidator implements ConstraintValidator<ValidListUpdateGroupPlayer, Object> {

    @Autowired
    private BettingGroupRepository bettingGroupRepo;
    private String REGEX_USER = ",";
    private String currentGroupIdField;
    private String fieldName;
    private String message;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void initialize(final ValidListUpdateGroupPlayer constraintAnnotation) {
        fieldName = constraintAnnotation.field();
        if (StringUtils.isEmpty(fieldName)) {
            fieldName = "";
        }
        currentGroupIdField = constraintAnnotation.currentGroupId();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            Long currentGroupId = Long.valueOf(FieldUtils.getFieldValue(value, currentGroupIdField).toString());

            if (currentGroupId == null || !bettingGroupRepo.findBettingGroupById(currentGroupId).isPresent()) {
                return false;
            }

            Optional<BettingGroup> bettingGroup = bettingGroupRepo.findBettingGroupById(currentGroupId);
            if (!bettingGroup.isPresent()) {
                throw new InvalidEmailUpdateBettingGroupException("Cannot find the group id");
            }


            List<String> listStringExistingPlayer = bettingGroup.get().getPlayers()
                .stream()
                .collect(Collectors.toList());

            List<String> listNewPlayersToUpdate = Arrays.stream((FieldUtils.getFieldValue(value, fieldName).toString().split(REGEX_USER)))
                .distinct()
                .collect(Collectors.toList());

            if (listNewPlayersToUpdate.containsAll(listStringExistingPlayer))
                return true;

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("players")
                .addConstraintViolation();

            return false;
        } catch (IllegalAccessException | InvalidEmailUpdateBettingGroupException ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("players")
                .addConstraintViolation();
            log.error("Cannot find the group by id", ex);
            return false;
        }
    }
}
