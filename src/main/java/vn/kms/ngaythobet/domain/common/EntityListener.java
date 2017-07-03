/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.domain.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import vn.kms.ngaythobet.domain.common.AuthService;
import vn.kms.ngaythobet.domain.common.BaseEntity;
import vn.kms.ngaythobet.domain.user.User;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Component
public class EntityListener implements ApplicationContextAware {
    private static ApplicationContext APP_CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        APP_CONTEXT = appContext;
    }

    @PrePersist
    public void prePersist(BaseEntity entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(getLogin());
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setModifiedAt(LocalDateTime.now());
        entity.setModifiedBy(getLogin());
    }

    private String getLogin() {
        AuthService authService = APP_CONTEXT.getBean(AuthService.class);
        if (authService == null) {
            return User.SYSTEM_USER;
        }
        return authService.getLogin();
    }
}
