package org.constraynt.file;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.constraynt.file.fileType.ValidateFile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    public static class Dto {
        @ValidateFile(allowedTypes = {"txt", "pdf"})
        public org.springframework.web.multipart.MultipartFile file;
    }

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (factory != null) factory.close();
    }

    @Test
    void nullOrEmpty_isValid() {
        Dto dto = new Dto();
        dto.file = null;
        assertTrue(validator.validate(dto).isEmpty());
        dto.file = new MockMultipartFile("file", new byte[0]);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void allowedExtension_passes() {
        Dto dto = new Dto();
        dto.file = new MockMultipartFile("file", "readme.txt", "text/plain", "hi".getBytes());
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void disallowedExtension_fails() {
        Dto dto = new Dto();
        dto.file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1,2});
        assertFalse(validator.validate(dto).isEmpty());
    }
}
