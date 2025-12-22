package com.tomcvt.pixelmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tomcvt.pixelmate.auth.AuthFailureHandler;
import com.tomcvt.pixelmate.network.RateLimitingFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final AuthFailureHandler authFailureHandler;
    private final RateLimitingFilter rateLimitingFilter;

    private static final String[] WHITELIST = {
        "/**", "/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
        "/login", "/error", "/api/public/**"
    };

    public SecurityConfig(AuthFailureHandler authFailureHandler, RateLimitingFilter rateLimitingFilter) {
        this.authFailureHandler = authFailureHandler;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .httpBasic(httpBasic -> httpBasic.disable())
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers(WHITELIST).permitAll()
                .anyRequest().permitAll()
            )
            .formLogin(login -> login.loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", false)
                .failureHandler(authFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .addFilterAfter(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .anonymous(Customizer.withDefaults());
        return http.build();
    }
}
