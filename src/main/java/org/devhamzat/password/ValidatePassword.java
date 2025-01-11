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
    /**
     * Defines the error message to be used when the password validation fails.
     *
     * @return The error message.
     */
    String message() default "Invalid password";

    /**
     * Defines the validation groups to which this constraint belongs.
     *
     * @return The validation groups.
     */

    Class<?>[] groups() default {};

    /**
     * Defines the payload associated with the constraint.
     *
     * @return The payload.
     */
    Class<? extends Payload>[] payload() default {};
}
