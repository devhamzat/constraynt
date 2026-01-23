package org.constraynt.password;

/**
 * Interface for password validation in the Constraynt library.
 * Implementations of this interface provide custom password validation logic.
 */
public interface ConstrayntPasswordValidator {
    /**
     * Validates the given password against the provided policy.
     * @param rawPassword the password text to validate.
     * @param policy the policy describing rule configuration.
     * @return validation result including message codes when invalid.
     */
    PasswordValidationResult validate(String rawPassword, PasswordPolicy policy);
}
