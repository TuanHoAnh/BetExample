package vn.kms.ngaythobet.infras.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.CompetitionRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by phuvha on 4/11/2017.
 */
@Slf4j
public class ValidCompetitorValidator implements ConstraintValidator<ValidCompetitor, Object> {
    @Autowired
    CompetitionRepository competitionRepository;
    private String message = "";
    @PersistenceContext
    private EntityManager em;

    @Override
    public void initialize(final ValidCompetitor constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {

            String name = (String) FieldUtils.getFieldValue(value, "name");
            Long id = (Long) FieldUtils.getFieldValue(value,"id");
            Long competitionId = (Long) FieldUtils.getFieldValue(value, "competitionId");
            Competition competition = competitionRepository.findOne(competitionId);
            String queryString = "select name from Competitor where name = :competitorName and competition = :competition";
            boolean isValid;
            if (id != null) {
                queryString += " and id != :id";
                isValid = em // case update , allow duplicate name, if id is equal the updating competitor
                    .createQuery(queryString)
                    .setParameter("competitorName", name)
                    .setParameter("competition", competition)
                    .setParameter("id",id)
                    .getResultList()
                    .isEmpty();
            } else {
                isValid = em // case create new, not allow duplicate name
                    .createQuery(queryString)
                    .setParameter("competitorName", name)
                    .setParameter("competition", competition)
                    .getResultList()
                    .isEmpty();
            }
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            }
            return isValid;
        } catch (IllegalAccessException e) {
            log.error("Validate error");
        }
        return false;
    }
}
