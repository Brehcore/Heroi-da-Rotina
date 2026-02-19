package br.com.coretech.hero_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
