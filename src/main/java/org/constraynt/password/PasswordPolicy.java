package org.constraynt.password;

import java.util.List;

public class PasswordPolicy {
    public final int minLength;
    public final int maxLength;
    public final boolean requireUppercase;
    public final boolean requireLowercase;
    public final boolean requireDigit;
    public final boolean requireSpecial;
    public final boolean disallowWhitespace;
    public final List<String> blockedSubstrings;
    public final boolean enableDefaultDictionary;
    public final boolean isDictionaryEnabled;
    public final boolean isBlockedStringsEnabled;

    public PasswordPolicy(int minLength, int maxLength,
                          boolean requireUppercase, boolean requireLowercase,
                          boolean requireDigit, boolean requireSpecial,
                          boolean disallowWhitespace,
                          List<String> blockedSubstrings,
                          boolean enableDefaultDictionary,
                          boolean isDictionaryEnabled,
                          boolean isBlockedStringsEnabled

    ) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.requireUppercase = requireUppercase;
        this.requireLowercase = requireLowercase;
        this.requireDigit = requireDigit;
        this.requireSpecial = requireSpecial;
        this.disallowWhitespace = disallowWhitespace;
        this.blockedSubstrings = blockedSubstrings;
        this.enableDefaultDictionary = enableDefaultDictionary;
        this.isDictionaryEnabled = isDictionaryEnabled;
        this.isBlockedStringsEnabled = isBlockedStringsEnabled;
    }
}
