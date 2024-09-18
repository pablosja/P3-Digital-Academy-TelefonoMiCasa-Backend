package com.factoriaf5.telefono_micasa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import com.factoriaf5.telefono_micasa.services.JpaUserDetailsService;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    
    @Value("${api-endpoint}") //ya se refiere al endpoint /api/v1, no es necesario cambiarlo. 
    String endpoint;

    MyBasicAuthenticationEntryPoint myBasicAuthenticationEntryPoint;
    
    JpaUserDetailsService jpaUserDetailsService;

    
    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, MyBasicAuthenticationEntryPoint basicEntryPoint) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.myBasicAuthenticationEntryPoint = basicEntryPoint;

}
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        .cors(cors -> cors.configurationSource(corsConfiguration()))
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
            .requestMatchers(HttpMethod.GET, endpoint + "/search/action_type").permitAll()
            .requestMatchers(HttpMethod.GET, endpoint + "/login").hasAnyRole("ADMIN")
            .anyRequest().authenticated())

        .userDetailsService(jpaUserDetailsService)
        .httpBasic(basic-> basic.authenticationEntryPoint(myBasicAuthenticationEntryPoint))
        .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.headers(header -> header.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

      @Bean
        CorsConfigurationSource corsConfiguration() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowCredentials(true);
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Desactiva el encriptado
    }
}