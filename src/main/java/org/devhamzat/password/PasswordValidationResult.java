package org.constraynt.password;

import java.util.Collections;
import java.util.List;

public record PasswordValidationResult(boolean valid, List<String> messageCodes) {
    public PasswordValidationResult(boolean valid, List<String> messageCodes) {
        this.valid = valid;
        this.messageCodes = messageCodes == null ? List.of() : List.copyOf(messageCodes);
    }

    @Override
    public List<String> messageCodes() {
        return Collections.unmodifiableList(messageCodes);
    }

    public static PasswordValidationResult ok() {
        return new PasswordValidationResult(true, List.of());
    }

    public static PasswordValidationResult fail(List<String> messageCodes) {
        return new PasswordValidationResult(false, messageCodes);
    }
}
