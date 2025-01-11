package org.devhamzat.file.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageFileValidator implements ConstraintValidator<ValidateImage, MultipartFile> {
    private long maxSize;
    private int maxWidth;
    private int maxHeight;
    private List<String> imageTypes;

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        if (value.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size exceeds maximum allowed" + maxSize / 1024 + "kb").addConstraintViolation();
            return false;
        }
        if (!imageTypes.contains(value.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File type not supported").addConstraintViolation();
            return false;
        }
        try {
            BufferedImage image = ImageIO.read(value.getInputStream());
            if (image == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid image file").addConstraintViolation();
                return false;
            }
            int width = image.getWidth();
            int height = image.getHeight();
            if (width > maxWidth || height > maxHeight) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Image dimensions exceeded").addConstraintViolation();
                return false;
            }
        } catch (IOException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Could not read image file").addConstraintViolation();
            return false;
        }
        return true;
    }

    @Override
    public void initialize(ValidateImage constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.maxWidth = constraintAnnotation.maxWidth();
        this.maxHeight = constraintAnnotation.maxHeight();
        this.imageTypes = Arrays.asList(constraintAnnotation.imageType());
    }
}
