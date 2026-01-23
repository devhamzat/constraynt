package org.constraynt.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.util.DomainNameUtil;

import java.net.IDN;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class Email implements ConstraintValidator<ValidateEmail, CharSequence> {
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final int MAX_DOMAIN_TOTAL_LENGTH = 255;
    private static final int MAX_LABEL_LENGTH = 63;

    // Fast path ASCII email regex (common case)
    private static final Pattern FAST_PATH_ASCII = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@" +
            "(?:(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)\\.)+" +
            "[A-Za-z]{2,63}$"
    );

    private static final String LOCAL_PART_ATOM_ALL = "[a-z0-9!#$%&'*+/=?^_`{|}~\\u0080-\\uFFFF-]";
    private static final String LOCAL_PART_ATOM_NO_PLUS = "[a-z0-9!#$%&'*//=?^_`{|}~\\u0080-\\uFFFF-]"; // no '+'
    private static final String LOCAL_PART_INSIDE_QUOTES_ATOM = "(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\\u0080-\\uFFFF-]|\\\\\\\\|\\\\\\\")";

    private Pattern localPartPattern;
    private boolean allowTld;
    private boolean allowIpDomain;
    private boolean allowPlusSign;

    @Override
    public void initialize(ValidateEmail constraintAnnotation) {
        this.allowTld = constraintAnnotation.allowTld();
        this.allowIpDomain = constraintAnnotation.allowIpDomain();
        this.allowPlusSign = constraintAnnotation.allowPlusSign();
        String atom = allowPlusSign ? LOCAL_PART_ATOM_ALL : LOCAL_PART_ATOM_NO_PLUS;
        this.localPartPattern = Pattern.compile(
                "(?:" + atom + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" +
                        "(?:\\." + "(?:" + atom + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" + ")*",
                CASE_INSENSITIVE
        );
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null || value.length() == 0) {
            return true; // null/empty considered valid; use @NotBlank to require
        }
        final String email = value.toString();

        // Fast path for common ASCII emails (only when TLDs are allowed)
        if (allowTld && FAST_PATH_ASCII.matcher(email).matches()) {
            return true;
        }

        int at = email.lastIndexOf('@');
        if (at <= 0 || at == email.length() - 1) {
            return violation(context, "{constraynt.email.missingAt}");
        }

        String local = email.substring(0, at);
        String domain = email.substring(at + 1);

        if (!isValidLocal(local)) {
            return violation(context, "{constraynt.email.local.invalid}");
        }

        if (!isValidDomain(domain, context)) {
            return false; // violation already added
        }
        return true;
    }

    private boolean isValidLocal(String local) {
        if (local.length() > MAX_LOCAL_PART_LENGTH) {
            return false;
        }
        // Disallow leading/trailing dot and consecutive dots when not quoted
        if (!(local.startsWith("\"") && local.endsWith("\""))) {
            if (local.startsWith(".") || local.endsWith(".")) return false;
            if (local.contains("..")) return false;
        }
        Matcher m = localPartPattern.matcher(local);
        return m.matches();
    }

    private boolean isValidDomain(String domain, ConstraintValidatorContext context) {
        // IP literal: [x.x.x.x] or [IPv6:...]
        if (domain.startsWith("[") && domain.endsWith("]")) {
            if (!allowIpDomain) {
                return violation(context, "{constraynt.email.domain.ipNotAllowed}");
            }
            String inner = domain.substring(1, domain.length() - 1);
            if (inner.regionMatches(true, 0, "IPv6:", 0, 5)) {
                String ipv6 = inner.substring(5);
                if (!IPV6_PATTERN.matcher(ipv6).matches()) {
                    return violation(context, "{constraynt.email.domain.invalid}");
                }
                return true;
            } else {
                if (!IPV4_PATTERN.matcher(inner).matches()) {
                    return violation(context, "{constraynt.email.domain.invalid}");
                }
                return true;
            }
        }

        String[] labels = domain.split("\\.");
        if (labels.length == 0) {
            return violation(context, "{constraynt.email.domain.invalid}");
        }
        if (!allowTld && labels.length < 2) {
            return violation(context, "{constraynt.email.domain.tldNotAllowed}");
        }

        StringBuilder asciiBuilder = new StringBuilder();
        try {
            for (int i = 0; i < labels.length; i++) {
                String label = labels[i];
                if (label.isEmpty()) {
                    return violation(context, "{constraynt.email.domain.invalid}");
                }
                String ascii = IDN.toASCII(label, IDN.USE_STD3_ASCII_RULES);
                if (ascii.isEmpty() || ascii.length() > MAX_LABEL_LENGTH) {
                    return violation(context, "{constraynt.email.domain.labelTooLong}");
                }
                if (!DOMAIN_LABEL_ASCII.matcher(ascii).matches()) {
                    return violation(context, "{constraynt.email.domain.invalid}");
                }
                if (i > 0) asciiBuilder.append('.');
                asciiBuilder.append(ascii);
            }
        } catch (IllegalArgumentException ex) {
            return violation(context, "{constraynt.email.domain.invalid}");
        }

        String asciiDomain = asciiBuilder.toString();
        if (asciiDomain.length() > MAX_DOMAIN_TOTAL_LENGTH) {
            return violation(context, "{constraynt.email.domain.tooLong}");
        }
        if (!DomainNameUtil.isValidEmailDomainAddress(asciiDomain)) {
            return violation(context, "{constraynt.email.domain.invalid}");
        }
        return true;
    }

    private boolean violation(ConstraintValidatorContext context, String messageTemplate) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation();
        return false;
    }

    // Conservative IPv4 and IPv6 patterns
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)){3}$");

    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^([0-9A-Fa-f]{1,4})(:([0-9A-Fa-f]{1,4})){7}$|^(([0-9A-Fa-f]{1,4}:){1,7}:)$|^(:(:[0-9A-Fa-f]{1,4}){1,7})$|^((([0-9A-Fa-f]{1,4}:){1,6}|:):([0-9A-Fa-f]{1,4})(:([0-9A-Fa-f]{1,4})){0,5})$");

    private static final Pattern DOMAIN_LABEL_ASCII = Pattern.compile(
            "^(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)$");
}
