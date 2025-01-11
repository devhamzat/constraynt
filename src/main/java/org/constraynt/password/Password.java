package org.constraynt.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of ConstraintValidator for password validation.
 * This class validates passwords annotated with {@link ValidatePassword} annotation.
 * It uses {@link DefaultConstrayntPasswordValidator} for the actual password validation logic.
 */
public class Password implements ConstraintValidator<ValidatePassword, String> {

    /**
     * The default password validator used for validating passwords.
     */
    @Autowired
    private DefaultConstrayntPasswordValidator defaultConstrayntPasswordValidator;

    /**
     * Validates the given password.
     *
     * @param password The password to validate. Can be null.
     * @param context The constraint validator context.
     * @return true if the password is valid, false otherwise.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            addConstraintViolation(context, "Password cannot be null");
            return false;
        }

        boolean isValid = defaultConstrayntPasswordValidator.validate(password);
        if (!isValid) {
            addConstraintViolation(context, String.join(", ", defaultConstrayntPasswordValidator.getErrorMessages()));
        }
        return isValid;
    }

    /**
     * Adds a constraint violation to the context.
     *
     * @param context The constraint validator context.
     * @param message The error message to add.
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}