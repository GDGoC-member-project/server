package com.gdgoc.member.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/hello", "/oauth2/**", "/api/v1/auth/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/projects/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/**").authenticated()

                        .requestMatchers("/api/v1/me/**").authenticated()

                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .defaultSuccessUrl("/api/v1/me/account", true)
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }
}

