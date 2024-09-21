package org.constraynt.password;

import java.util.List;

public interface ConstrayntPasswordValidator {
    boolean validate(String password);
    List<String> getErrorMessages();

}
