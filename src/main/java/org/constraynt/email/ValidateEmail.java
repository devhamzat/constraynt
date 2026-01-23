package org.constraynt.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(value = RUNTIME)
@Constraint(validatedBy = Email.class)
@Documented
public @interface ValidateEmail {
    String message() default "{org.constraynt.email.ValidateEmail}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowTld() default true;

    boolean allowIpDomain() default false;

    boolean allowPlusSign() default true;

}
