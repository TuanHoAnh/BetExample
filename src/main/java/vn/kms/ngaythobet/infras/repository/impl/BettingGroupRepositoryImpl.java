/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.infras.repository.impl;

import org.springframework.stereotype.Repository;
import vn.kms.ngaythobet.domain.betting.BettingGroupRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by thangvtran on 4/10/2017.
 */
@Repository
public class BettingGroupRepositoryImpl implements BettingGroupRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void searchBettingGroup() {
        // ...
    }


}
