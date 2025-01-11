package org.constraynt.password;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Configuration class for setting up the default password validator bean.
 */
@Configuration
public class PasswordValidatorConfig {
    /**
     * Creates and configures the default ConstrayntPasswordValidator bean.
     *
     * @return A new instance of DefaultConstrayntPasswordValidator.
     */
    @Bean
    @ConditionalOnMissingBean(DefaultConstrayntPasswordValidator.class)
    public DefaultConstrayntPasswordValidator defaultConstrayntPasswordValidator() {
        return new DefaultConstrayntPasswordValidator();
    }
}
