package com.tomcvt.pixelmate.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class AuthConfig {
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthConfig.class);
    private final PasswordEncoder passwordEncoder;
    private final String superuserName;
    private final String superuserPassword;
    public AuthConfig(@Value("${pixelmate.security.superuser.name}") String superuserName,
                      @Value("${pixelmate.security.superuser.password}") String superuserPassword,
                      PasswordEncoder passwordEncoder) {
        this.superuserName = superuserName;
        this.superuserPassword = superuserPassword;
        this.passwordEncoder = passwordEncoder;
        log.info("Superuser configured: {}", superuserName);
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authProvider) {
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public AuthenticationProvider inMemoryAuthenticationProvider(InMemoryUserDetailsManager userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
        userDetailsService.createUser(
            org.springframework.security.core.userdetails.User.withUsername(superuserName)
                .passwordEncoder(passwordEncoder::encode)
                .password(superuserPassword)
                .roles("ADMIN")
                .build()
        );
        return userDetailsService;
    }
    
}
