package vn.kms.ngaythobet.infras.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidFileTypeValidator implements ConstraintValidator<ValidFileType, Object> {
    private String validFile;
    private String message;
    @Override
    public void initialize(ValidFileType annotation) {
        validFile = annotation.validFileType();
        message = annotation.message();
    }
    private String getFileType(String fileName) {
        String[] arr = fileName.split("\\.");
        return arr[arr.length-1];
    }

    @Override
    public boolean isValid(Object logo, ConstraintValidatorContext constraintValidatorContext) {
        String fileType = getFileType(logo.toString());
        return (validFile.contains(fileType));
    }
}
