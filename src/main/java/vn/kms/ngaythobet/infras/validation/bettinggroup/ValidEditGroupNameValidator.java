/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation.bettinggroup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import org.springframework.util.StringUtils;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.service.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@Slf4j
public class ValidEditGroupNameValidator implements ConstraintValidator<ValidEditGroupName, Object> {

    private String currentGroupIdField;
    private String newGroupNameField;
    private Class<? extends BaseEntity> entity;
    private String fieldName;
    private String message;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void initialize(final ValidEditGroupName constraintAnnotation) {
        entity = constraintAnnotation.entity();
        fieldName = constraintAnnotation.field();
        if (StringUtils.isEmpty(fieldName)) {
            fieldName = "";
        }
        currentGroupIdField = constraintAnnotation.currentGroupId();
        newGroupNameField = constraintAnnotation.newGroupName();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            Long currentGroupId = Long.valueOf(FieldUtils.getFieldValue(value, currentGroupIdField).toString());
            String newGroupName = FieldUtils.getFieldValue(value, newGroupNameField).toString().trim();


            String queryOldGroupNameString = String.format("select %s from %s where %s = :fieldValue", fieldName, entity.getName(), currentGroupIdField);
            String oldGroupName = (String) em
                .createQuery(queryOldGroupNameString)
                .setParameter("fieldValue", currentGroupId)
                .getSingleResult();
            if (oldGroupName.equals(newGroupName))
                return true;

            String queryNewGroupNameStringExisting = String.format("select %s from %s where %s = :fieldValue", fieldName, entity.getName(), fieldName);
            boolean isEmptyNewGroupName = em
                .createQuery(queryNewGroupNameStringExisting)
                .setParameter("fieldValue", newGroupName)
                .getResultList()
                .isEmpty();

            if (isEmptyNewGroupName)
                return true;


            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(newGroupNameField)
                .addConstraintViolation();


            return false;
        } catch (IllegalAccessException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(newGroupNameField)
                .addConstraintViolation();
            log.error("Invalid betting group name update - Cannot access value", e);
            return false;
        }
    }
}
