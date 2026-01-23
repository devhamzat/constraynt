package org.constraynt.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.Arrays;

/**
 * ConstraintValidator for @ValidatePassword using a pluggable ConstrayntPasswordValidator.
 */
public class Password implements ConstraintValidator<ValidatePassword, String> {

    @Autowired
    private ConstrayntPasswordValidator validator;

    private ValidatePassword annotation;

    @Override
    public void initialize(ValidatePassword constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Presence handling according to `required` flag
        if (value == null || value.isEmpty()) {
            if (annotation.required()) {
                // Emit presence-specific message and fail
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{constraynt.password.null}")
                        .addConstraintViolation();
                return false;
            } else {
                // Not required: empty is allowed; skip further checks
                return true;
            }
        }

        PasswordPolicy policy = new PasswordPolicy(
                annotation.minLength(),
                annotation.maxLength(),
                annotation.requireUppercase(),
                annotation.requireLowercase(),
                annotation.requireDigit(),
                annotation.requireSpecial(),
                annotation.disallowWhitespace(),
                Arrays.asList(annotation.blockedSubstrings()),
                annotation.enableDefaultDictionary(),
                annotation.isBlockedStringsEnabled(),
                annotation.isDictionaryEnabled()
        );

        if (validator == null) {
            throw new IllegalStateException("ConstrayntPasswordValidator bean is not initialized. Ensure Spring is configuring ConstraintValidator instances or provide a validator.");
        }

        PasswordValidationResult result = validator.validate(value, policy);
        if (result.valid()) {
            return true;
        }

        // Prepare to add message parameters for interpolation (min/max)
        context.disableDefaultConstraintViolation();
        HibernateConstraintValidatorContext hctx = null;
        try {
            hctx = context.unwrap(HibernateConstraintValidatorContext.class);
            hctx = hctx.addMessageParameter("minLength", policy.minLength)
                       .addMessageParameter("maxLength", policy.maxLength);
        } catch (Exception ignored) {
            // Not running with Hibernate Validator; fall back without parameters
        }

        if (annotation.exposeAllViolations()) {
            for (String code : result.messageCodes()) {
                if (hctx != null) {
                    hctx.buildConstraintViolationWithTemplate("{" + code + "}")
                        .addConstraintViolation();
                } else {
                    context.buildConstraintViolationWithTemplate("{" + code + "}")
                           .addConstraintViolation();
                }
            }
        } else {
            String first = result.messageCodes().isEmpty() ? "constraynt.password.invalid" : result.messageCodes().get(0);
            if (hctx != null) {
                hctx.buildConstraintViolationWithTemplate("{" + first + "}")
                    .addConstraintViolation();
            } else {
                context.buildConstraintViolationWithTemplate("{" + first + "}")
                       .addConstraintViolation();
            }
        }
        return false;
    }
}