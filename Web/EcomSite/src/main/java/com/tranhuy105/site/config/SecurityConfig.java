package com.tranhuy105.site.config;

import com.tranhuy105.site.security.CustomerOAuth2UserService;
import com.tranhuy105.site.security.EmailLoginSuccessHandler;
import com.tranhuy105.site.security.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain configureHttpSecurity(HttpSecurity http,
                                              OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                                              EmailLoginSuccessHandler emailLoginSuccessHandler,
                                              CustomerOAuth2UserService oAuth2UserService) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/customer/**", "/order/**").authenticated()
                .anyRequest().permitAll())
                .csrf(csrf -> csrf
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    )
                .formLogin(form-> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .permitAll()
                        .successHandler(emailLoginSuccessHandler))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .logout(LogoutConfigurer::permitAll)
                .rememberMe(rem -> rem
                        .key("PleaseREMBEMBERME")
                        .tokenValiditySeconds(24 * 60 * 60));


        return http.build();
    }
}
