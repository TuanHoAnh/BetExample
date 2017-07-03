/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import org.springframework.util.StringUtils;
import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldUniqueValidator implements ConstraintValidator<FieldUnique, Object> {

    private Class<? extends BaseEntity> entity;

    private String fieldName;
    private String message;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void initialize(FieldUnique annotation) {
        entity = annotation.entity();
        fieldName = annotation.field();
        if (StringUtils.isEmpty(fieldName)) {
            fieldName = "";
        }
        message = annotation.message();
    }

    @Override
    public boolean isValid(Object fieldValue, ConstraintValidatorContext context) {
            String queryString = String.format("select %s from %s where lower(%s) = lower(:fieldValue)", fieldName, entity.getName(), fieldName);

            boolean isValid = em
                    .createQuery(queryString)
                    .setParameter("fieldValue", fieldValue)
                    .getResultList()
                    .isEmpty();
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation();
            }
            return isValid;
    }
}
