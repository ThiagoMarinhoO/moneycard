package com.example.moneycard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers("moneycards/**").hasRole("CARD-OWNER").and().csrf().disable().httpBasic();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();

        UserDetails th = users
                .username("th")
                .password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER") // No roles for now
                .build();

        UserDetails marcos = users
                .username("marcos-owns-no-card")
                .password(passwordEncoder.encode("qwe123"))
                .roles("NON-OWNER") // No roles for now
                .build();

        UserDetails lucas = users
                .username("lucas")
                .password(passwordEncoder.encode("asd123"))
                .roles("CARD-OWNER") // No roles for now
                .build();

        return new InMemoryUserDetailsManager(th, marcos, lucas);
    }
}
