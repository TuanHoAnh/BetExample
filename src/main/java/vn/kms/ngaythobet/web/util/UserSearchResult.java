package vn.kms.ngaythobet.web.util;

import lombok.Data;

/**
 * Created by vuongvu on 4/18/2017.
 */
@Data
public class UserSearchResult {

    private String value;
    private String label;

    public UserSearchResult(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
