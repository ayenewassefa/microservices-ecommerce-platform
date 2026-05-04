package com.ACT.discoveryserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class EurekaSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Required for Eureka clients to register
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Everything requires login
                )
                .formLogin(withDefaults()) // This enables the /login page for your browser
                .httpBasic(withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")             // The URL to trigger logout
                        .invalidateHttpSession(true)      // Destroy the session
                        .deleteCookies("JSESSIONID")     // Delete the security cookie
                        .logoutSuccessUrl("/login?logout") // Where to go after logging out
                );



        return http.build(); // Only ONE return statement at the end
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username("eureka")
                .password("{noop}password") // {noop} is required for plain text in Spring Boot 3
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}