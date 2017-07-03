package vn.kms.ngaythobet.web.util;

import lombok.Data;

@Data
public class EmailValidate {
    private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
    private static final String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)+";
    private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    static final String EMAIL_PATTERN =
        "^" + ATOM + "+(\\." + ATOM + "+)*@"
            + DOMAIN
            + "|"
            + IP_DOMAIN
            + ")$";

    public static boolean isValidEmail(String email) {

        return email.toLowerCase().matches(EMAIL_PATTERN);
    }
}
