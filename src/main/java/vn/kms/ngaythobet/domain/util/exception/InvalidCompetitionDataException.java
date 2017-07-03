package vn.kms.ngaythobet.domain.util.exception;


public class InvalidCompetitionDataException extends Exception {
    public InvalidCompetitionDataException(String message, Exception e) {
        super(message, e);
    }

    public InvalidCompetitionDataException(String message) {
        super(message);
    }
}
