package org.constraynt.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class Password implements ConstraintValidator<ValidatePassword, String> {
    @Autowired
    private DefaultConstrayntPasswordValidator defaultConstrayntPasswordValidator;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password cannot be null").addConstraintViolation();
            return false;
        }
        boolean isValid = defaultConstrayntPasswordValidator.validate(password);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.join(", ", defaultConstrayntPasswordValidator.getErrorMessages()))
                    .addConstraintViolation();
        }
        return isValid;
    }
}

