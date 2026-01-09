package com.gdgoc.member.security;

import org.springframework.security.config.Customizer;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgoc.member.BaseResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // CORS 
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            BaseResponse<?> body = BaseResponse.error(
                                    "UNAUTHORIZED",
                                    "Invalid or missing token.",
                                    null
                            );
                            try {
                                new ObjectMapper().writeValue(response.getWriter(), body);
                            } catch (Exception ex) {
                            }
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/",
                                "/hello",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/oauth2/**",
                                "/api/v1/auth/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/projects/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/**").authenticated()

                        .requestMatchers("/api/v1/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/profiles/upload_image").authenticated()
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://gdgoc-seoultech.vercel.app"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
