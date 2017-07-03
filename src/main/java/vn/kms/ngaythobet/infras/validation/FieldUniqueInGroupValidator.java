/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldUniqueInGroupValidator implements ConstraintValidator<FieldUniqueInGroup, Object> {
    private String nameField;

    private String message;

    private String groupIdField;

    @Autowired
    private CompetitionRepository competitionRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void initialize(final FieldUniqueInGroup constraintAnnotation) {
        nameField = constraintAnnotation.name();
        groupIdField = constraintAnnotation.groupId();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, ConstraintValidatorContext context){
        try {
            String name = FieldUtils.getFieldValue(value, nameField).toString();
            String competitionAliasKey = FieldUtils.getFieldValue(value, groupIdField).toString();
            Competition competition = competitionRepo.findOneByAliasKey(competitionAliasKey).get();
            String queryString = String.format("select name from BettingGroup where competition = :competition and name = :name");

            boolean isValid = entityManager
                .createQuery(queryString)
                .setParameter("competition", competition)
                .setParameter("name", name)
                .getResultList()
                .isEmpty();
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(nameField)
                    .addConstraintViolation();
            }
            return isValid;
        } catch (IllegalAccessException ex) {
        throw new RuntimeException("Could not access form field", ex);
    }

    }
}
