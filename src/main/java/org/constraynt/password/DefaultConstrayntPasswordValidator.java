package org.constraynt.password;

import org.passay.*;
import org.passay.dictionary.Dictionary;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of the ConstrayntPasswordValidator interface using Passay.
 */
public class DefaultConstrayntPasswordValidator implements ConstrayntPasswordValidator {

    private static final String[] DEFAULT_DICT = {
            "password", "passw0rd", "letmein", "welcome", "admin", "user",
            "qwerty", "abc123", "iloveyou", "monkey"
    };

    @Override
    public PasswordValidationResult validate(String rawPassword, PasswordPolicy policy) {
        List<Rule> rules = new ArrayList<>();
        rules.add(new LengthRule(policy.minLength, policy.maxLength));
        if (policy.requireUppercase) rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        if (policy.requireLowercase) rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        if (policy.requireDigit) rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
        if (policy.requireSpecial) rules.add(new CharacterRule(EnglishCharacterData.Special, 1));
        if (policy.disallowWhitespace) {
            rules.add(new WhitespaceRule());
        }
        if (policy.isBlockedStringsEnabled) {
            if (policy.blockedSubstrings != null && !policy.blockedSubstrings.isEmpty()) {
                rules.add(new IllegalRegexRule(buildSubstringUnionRegex(policy.blockedSubstrings)));
            }
        }
        if (policy.isDictionaryEnabled) {
            Dictionary dictionary = buildCombinedDictionary(policy.enableDefaultDictionary);
            if (dictionary != null) {
                rules.add(new DictionaryRule(dictionary));
            }
        }

        PasswordValidator validator = new PasswordValidator(rules);
        RuleResult result = validator.validate(new PasswordData(rawPassword));
        if (result.isValid()) {
            return PasswordValidationResult.ok();
        }

        List<String> codes = result.getDetails().stream()
                .map(this::mapDetailToMessageKey)
                .distinct()
                .collect(Collectors.toList());

        if (codes.isEmpty()) {
            codes = List.of("constraynt.password.invalid");
        }
        return PasswordValidationResult.fail(codes);
    }

    private String buildSubstringUnionRegex(List<String> blocked) {
        String inner = blocked.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(java.util.regex.Pattern::quote)
                .collect(Collectors.joining("|"));
        if (inner.isEmpty()) inner = "(?!)";
        return "(?i).*(" + inner + ").*";
    }

    private Dictionary buildCombinedDictionary(boolean includeDefault) {
        List<String> all = new ArrayList<>();
        if (includeDefault) all.addAll(Arrays.asList(DEFAULT_DICT));

        ServiceLoader<org.constraynt.password.spi.DictionaryProvider> loader = ServiceLoader.load(org.constraynt.password.spi.DictionaryProvider.class);
        for (org.constraynt.password.spi.DictionaryProvider provider : loader) {
            Collection<String> words = provider.words();
            if (words != null) all.addAll(words);
        }
        if (all.isEmpty()) return null;

        try {
            return new WordListDictionary(WordLists.createFromReader(
                    all.stream().map(s -> new java.io.StringReader(s + "\n")).toArray(java.io.Reader[]::new),
                    false
            ));
        } catch (java.io.IOException e) {
            // Fallback: no dictionary if we cannot build
            return null;
        }
    }

    private String mapDetailToMessageKey(RuleResultDetail d) {
        String code = d.getErrorCode();
        if ("TOO_SHORT".equals(code)) return "constraynt.password.minLength";
        if ("TOO_LONG".equals(code)) return "constraynt.password.maxLength";
        if ("INSUFFICIENT_UPPERCASE".equals(code)) return "constraynt.password.uppercase";
        if ("INSUFFICIENT_LOWERCASE".equals(code)) return "constraynt.password.lowercase";
        if ("INSUFFICIENT_DIGIT".equals(code)) return "constraynt.password.digit";
        if ("INSUFFICIENT_SPECIAL".equals(code)) return "constraynt.password.special";
        if ("ILLEGAL_WHITESPACE".equals(code)) return "constraynt.password.whitespace";
        if ("ILLEGAL_WORD".equals(code)) return "constraynt.password.dictionary";
        if ("ILLEGAL_MATCH".equals(code)) return "constraynt.password.blockedSubstring";
        return "constraynt.password.invalid";
    }
}
