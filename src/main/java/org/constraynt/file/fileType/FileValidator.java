package org.constraynt.file.fileType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileValidator implements ConstraintValidator<ValidateFile, MultipartFile> {
    private String[] allowedFiles;

    @Override
    public void initialize(ValidateFile constraintAnnotation) {
        this.allowedFiles = constraintAnnotation.allowedTypes();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        String fileName = value.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String fileExtension = getFileExtension(fileName);
        return Arrays.stream(allowedFiles)
                .anyMatch(allowedType -> allowedType.equalsIgnoreCase(fileExtension));
    }

    private String getFileExtension(String file) {
        if (file.lastIndexOf('.') != -1 && file.lastIndexOf('.') != 0) {
            return file.substring(file.lastIndexOf('.') + 1);
        } else {
            return "";
        }

    }
}
