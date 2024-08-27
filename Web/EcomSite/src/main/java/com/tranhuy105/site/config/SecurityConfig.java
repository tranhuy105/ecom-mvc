package com.tranhuy105.site.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/customer/**").authenticated()
                .anyRequest().permitAll())
                .formLogin(form-> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .permitAll())
                .logout(LogoutConfigurer::permitAll)
                .rememberMe(rem -> rem
                        .key("PleaseREMBEMBERME")
                        .tokenValiditySeconds(24 * 60 * 60));


        return http.build();
    }
}
