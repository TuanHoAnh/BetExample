/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.validation.bettinggroup;

import org.springframework.beans.factory.annotation.Autowired;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.domain.user.User;
import vn.kms.ngaythobet.infras.validation.bettinggroup.HaveModeratorRole;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by vuongvu on 4/5/2017.
 */
public class HaveModeratorRoleValidator implements ConstraintValidator<HaveModeratorRole, Long> {
    @Autowired
    private AuthService authService;


    private String message;

    private Class<? extends BaseEntity> entity;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void initialize(HaveModeratorRole annotation) {
        message = annotation.message();
        entity = annotation.entity();
    }

    @Override
    public boolean isValid(Long fieldValue, ConstraintValidatorContext context) {


        try {
            String queryString = String.format("select %s from %s tb where %s =:fieldValue", "moderator", entity.getName(), "tb.id");

            User moderator = (User) em
                .createQuery(queryString)
                .setParameter("fieldValue", fieldValue)
                .getSingleResult();

            if (!authService.getLoginUser().isPresent()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("moderator")
                    .addConstraintViolation();

            }

            if (authService.getLoginUser().get().getId() == moderator.getId())
                return true;

            return false;

        }catch (Exception e)
        {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("moderator")
                .addConstraintViolation();
            return false;
        }

    }
}
