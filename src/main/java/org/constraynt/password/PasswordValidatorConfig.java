package org.constraynt.password;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordValidatorConfig {
    @Bean
    @ConditionalOnMissingBean(DefaultConstrayntPasswordValidator.class)
    public DefaultConstrayntPasswordValidator defaultConstrayntPasswordValidator() {
        return new DefaultConstrayntPasswordValidator();
    }
}
