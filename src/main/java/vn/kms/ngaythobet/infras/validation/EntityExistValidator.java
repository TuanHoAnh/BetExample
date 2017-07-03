/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation;

import vn.kms.ngaythobet.domain.common.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EntityExistValidator implements ConstraintValidator<EntityExist, Long> {

    private Class<? extends BaseEntity> entity;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void initialize(EntityExist annotation) {
        this.entity = annotation.type();
    }

    @Override
    public boolean isValid(Long entityId, ConstraintValidatorContext context) {
        Query query = em.createQuery("select id from " + entity.getName() + " where id=" + entityId);

        return !query.getResultList().isEmpty();
    }
}
