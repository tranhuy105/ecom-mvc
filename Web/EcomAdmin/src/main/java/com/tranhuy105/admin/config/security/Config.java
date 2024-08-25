package com.tranhuy105.admin.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class Config {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    SecurityFilterChain configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/**").hasAuthority("Admin")
                .requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
                .requestMatchers("/products/**").hasAnyAuthority("Admin", "Salesperson", "Editor", "Shipper")
                .requestMatchers("/images/**", "/js/**", "/webjars/**").permitAll()
                .anyRequest()
                .authenticated()
        ).formLogin(form-> form
                .loginPage("/login")
                .usernameParameter("email")
                .permitAll()
        ).logout(LogoutConfigurer::permitAll).rememberMe(rem -> rem
                .key("PleaseREMBEMBERME")
                .tokenValiditySeconds(24 * 60 * 60)
        );

        return http.build();
    }
}
