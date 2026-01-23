package org.constraynt.password.spi;

import java.util.Collection;

/**
 * SPI for providing additional dictionary words used by the password validator.
 */
public interface DictionaryProvider {
    Collection<String> words();
}
