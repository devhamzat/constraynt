package org.devhamzat.file.text;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TXTFileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateTXTFiles {
    String message() default "{org.constraynt.file.text.ValidateTXTFiles}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] txtTypes() default {};
}
