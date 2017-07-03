package vn.kms.ngaythobet.domain.util.exception;


public class InvalidEmailUpdateBettingGroupException extends Exception {
    public InvalidEmailUpdateBettingGroupException(String message, Exception e) {
        super(message, e);
    }

    public InvalidEmailUpdateBettingGroupException(String message) {
        super(message);
    }
}
