package org.constraynt.file.fileType;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidateFile {
    String message() default "Invalid file type";

    String[] allowedTypes() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
