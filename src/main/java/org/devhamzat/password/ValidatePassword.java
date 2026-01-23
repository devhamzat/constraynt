package org.devhamzat.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to be used on password fields for automatic validation.
 * This annotation triggers the use of the ConstrayntPasswordValidator.
 */
@Documented
@Constraint(validatedBy = Password.class)
@Target({FIELD, ANNOTATION_TYPE})
@Retention(value = RUNTIME)
public @interface ValidatePassword {
    // Use message key so consumers can localize
    String message() default "{org.constraynt.password.ValidatePassword}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    boolean required() default true;

    int minLength() default 8;

    int maxLength() default 64;

    boolean requireUppercase() default true;

    boolean requireLowercase() default true;

    boolean requireDigit() default true;

    boolean requireSpecial() default true;

    boolean disallowWhitespace() default true;

    boolean isBlockedStringsEnabled() default false;

    String[] blockedSubstrings() default {"password", "qwerty", "123456", "letmein"};

    boolean isDictionaryEnabled() default false;

    boolean enableDefaultDictionary() default true;


    boolean exposeAllViolations() default false;
}
