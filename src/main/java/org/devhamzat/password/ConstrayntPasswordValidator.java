package org.devhamzat.password;

import java.util.List;

/**
 * Interface for password validation in the Constraynt library.
 * Implementations of this interface provide custom password validation logic.
 */
public interface ConstrayntPasswordValidator {
    /**
     * Validates the given password against the implemented rules.
     *
     * @param password The password to validate.
     * @return true if the password is valid, false otherwise.
     */
    boolean validate(String password);
    /**
     * Retrieves the error messages generated during the last validation.
     *
     * @return A list of error messages, or an empty list if no errors occurred.
     */
    List<String> getErrorMessages();

}
