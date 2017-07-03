/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.form;

import lombok.Data;
import vn.kms.ngaythobet.domain.betting.BettingMatch;

import java.util.List;

@Data
public class BettingMatchRoundForm {
    private String name;

    private List<BettingMatch> matchList;
}
