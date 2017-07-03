package vn.kms.ngaythobet.infras.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.service.betting.BettingMatchService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by congle on 4/11/2017.
 */
@Slf4j
public class FieldUniqueBettingMatchValidator  implements ConstraintValidator<FieldUniqueBettingMatch, Object> {


    @Autowired
    private BettingMatchService bettingMatchService;

    private String matchIdField;
    private String groupIdField;
    private String bettingMatchIdField;
    private String message;

    @Override
    public void initialize(final FieldUniqueBettingMatch constraintAnnotation) {
        matchIdField = constraintAnnotation.matchId();
        groupIdField = constraintAnnotation.groupId();
        bettingMatchIdField = constraintAnnotation.bettingMatchId();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {

            if ("".equals(FieldUtils.getFieldValue(value, matchIdField)) || "".equals(FieldUtils.getFieldValue(value, groupIdField))){
                return false;
            }

            if (FieldUtils.getFieldValue(value, matchIdField) == null || FieldUtils.getFieldValue(value, groupIdField) == null){
                return false;
            }

            Long matchId = Long.parseLong(FieldUtils.getFieldValue(value, matchIdField).toString());
            Long groupId = Long.parseLong(FieldUtils.getFieldValue(value, groupIdField).toString());


            if (!"".equals(FieldUtils.getFieldValue(value, bettingMatchIdField))){
                Long bettingMatchId = Long.parseLong(FieldUtils.getFieldValue(value, bettingMatchIdField).toString());
                if (bettingMatchService.checkBettingMatchUserInput(matchId,groupId,bettingMatchId)){
                    return true;
                }
            }

            boolean isBettingMatchUnique = bettingMatchService.checkUniqueMatch(matchId,groupId);
            if (!isBettingMatchUnique) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{create.betting.match.fail}")
                    .addPropertyNode(matchIdField)
                    .addConstraintViolation();
            }

            return isBettingMatchUnique;
        } catch (Exception ex) {
            throw new RuntimeException("Could not compare field value of " + matchIdField + " and " + groupIdField, ex);
        }
    }
}
