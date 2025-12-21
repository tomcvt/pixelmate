package com.tomcvt.pixelmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.tomcvt.pixelmate.auth.AuthFailureHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final AuthFailureHandler authFailureHandler;

    private static final String[] WHITELIST = {
        "/**"
    };

    public SecurityConfig(AuthFailureHandler authFailureHandler) {
        this.authFailureHandler = authFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .httpBasic(httpBasic -> httpBasic.disable())
            .authorizeHttpRequests((auth) -> auth
                .anyRequest().permitAll()
            )
            .formLogin(login -> login.loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", false)
                .failureHandler(authFailureHandler)
                .permitAll()
            )
            .anonymous(Customizer.withDefaults());
        return http.build();
    }
}
