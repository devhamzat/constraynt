package org.constraynt.password;

import org.passay.*;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordListDictionary;

import java.util.Arrays;
import java.util.List;

public class DefaultConstrayntPasswordValidator implements ConstrayntPasswordValidator {
    private final PasswordValidator validator;
    private List<String> errorMessages;

    public DefaultConstrayntPasswordValidator() {
        WordListDictionary wordListDictionary = new WordListDictionary(
                new ArrayWordList(new String[]{"password", "username"}));
        validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 64),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new DictionarySubstringRule(wordListDictionary),
                new DictionaryRule(wordListDictionary),
                new WhitespaceRule()
        ));

    }

    @Override
    public boolean validate(String password) {
        RuleResult result = validator.validate(new PasswordData(password));
        errorMessages = validator.getMessages(result);
        return result.isValid();
    }

    @Override
    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
