/*
 * Copyright ( C ) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.util;

import lombok.Data;

/**
 * Created by thangvtran on 4/9/2017.
 */
@Data
public class Pair<F, S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
