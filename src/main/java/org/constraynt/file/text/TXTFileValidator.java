package org.constraynt.file.text;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TXTFileValidator implements ConstraintValidator<ValidateTXTFiles, MultipartFile> {
    private List<String> txtTypes;

    @Override
    public void initialize(ValidateTXTFiles constraintAnnotation) {
        this.txtTypes = Arrays.asList(constraintAnnotation.txtTypes());
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
     if (value == null||value.isEmpty()) {
         return true;
     }
     String contentType = value.getContentType();
     if (contentType != null && txtTypes.stream().anyMatch(contentType::equalsIgnoreCase)) {
         return true;
     }
     String fileName = value.getOriginalFilename();
        return fileName != null && txtTypes.stream().anyMatch(type -> fileName.toLowerCase(Locale.ROOT).endsWith(type));
    }
}
