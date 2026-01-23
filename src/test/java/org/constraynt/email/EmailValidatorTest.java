package org.constraynt.email;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EmailValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    public static class Dto {
        @ValidateEmail(allowTld = true, allowIpDomain = false, allowPlusSign = true)
        public String email;
    }

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (factory != null) factory.close();
    }

    @Test
    void nullAndEmpty_areValid() {
        Dto dto = new Dto();
        dto.email = null;
        assertTrue(validator.validate(dto).isEmpty());
        dto.email = "";
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void validAscii_shouldPass() {
        Dto dto = new Dto();
        dto.email = "user.name+tag@example.com";
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void missingAt_shouldFail() {
        Dto dto = new Dto();
        dto.email = "username.example.com";
        Set<ConstraintViolation<Dto>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
        assertTrue(v.iterator().next().getMessage().toLowerCase().contains("@"));
    }

    @Test
    void tldRequired_whenDisabled_shouldFail() {
        class DtoNoTld { @ValidateEmail(allowTld = false) public String email; }
        DtoNoTld dto = new DtoNoTld();
        dto.email = "user@localhost";
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void ipDomain_notAllowed_shouldFail() {
        Dto dto = new Dto();
        dto.email = "user@[127.0.0.1]";
        assertFalse(validator.validate(dto).isEmpty());
    }
}
