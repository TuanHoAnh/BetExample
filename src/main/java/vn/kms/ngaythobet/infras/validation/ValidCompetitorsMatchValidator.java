/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import vn.kms.ngaythobet.service.competition.MatchService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidCompetitorsMatchValidator implements ConstraintValidator<ValidCompetitorsMatch, Object> {

    @Autowired
    private MatchService matchService;

    private String competitor1Field;
    private String competitor2Field;
    private String roundField;
    private String matchIdField;



    @Override
    public void initialize(final ValidCompetitorsMatch constraintAnnotation) {
        competitor1Field = constraintAnnotation.competitor1Id();
        competitor2Field = constraintAnnotation.competitor2Id();
        roundField = constraintAnnotation.round();
        matchIdField = constraintAnnotation.matchId();

    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            if (FieldUtils.getFieldValue(value, competitor1Field) == null ||
                FieldUtils.getFieldValue(value, competitor2Field) == null){
                return true;
            }
            Long competitor1Id = Long.parseLong(FieldUtils.getFieldValue(value, competitor1Field).toString());
            Long competitor2Id = Long.parseLong(FieldUtils.getFieldValue(value, competitor2Field).toString());

            if (FieldUtils.getFieldValue(value, roundField) == null){

                return false;
            }
            String round = FieldUtils.getFieldValue(value, roundField).toString();

            if (FieldUtils.getFieldValue(value, matchIdField) != null){
                Long matchId = Long.parseLong(FieldUtils.getFieldValue(value, matchIdField).toString());
                if (matchService.isUniqueMatchInRoundUpdate(competitor1Id,competitor2Id,round,matchId)){
                    return true;
                }
            }

            boolean notMatched = !(competitor1Id != null && competitor1Id.equals(competitor2Id));
            if (!notMatched) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{create.match.competitors.same}")
                    .addPropertyNode(competitor2Field)
                    .addConstraintViolation();
            }

            boolean uniqueCompetitors = matchService.isUniqueMatchInRound(competitor1Id,competitor2Id,round);
            if (!uniqueCompetitors){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{create.match.competitors.unique}")
                    .addPropertyNode(competitor2Field)
                    .addConstraintViolation();
            }

            return notMatched && uniqueCompetitors;
        } catch (Exception ex) {
            throw new RuntimeException("Could not compare field value of " + competitor1Field + " and " + competitor2Field, ex);
        }
    }
}
