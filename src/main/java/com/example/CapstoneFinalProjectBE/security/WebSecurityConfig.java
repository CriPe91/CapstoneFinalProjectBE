package com.example.CapstoneFinalProjectBE.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    /**
     * 🔐 Bean per la codifica delle password con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 🔑 Configura l'AuthenticationManager per gestire l'autenticazione
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     *  Configura la sicurezza delle richieste HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Disabilitiamo CORS e CSRF perché useremo JWT (stateless)
        httpSecurity.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable());

        // Configuriamo i permessi di accesso alle API
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/create").permitAll() // 🔓 Permettiamo la registrazione senza autenticazione
                .requestMatchers("/user/login").permitAll() // 🔓 Permettiamo il login senza autenticazione
                .requestMatchers("/user/auth/**").hasRole("USER") // 🔒 Solo gli utenti autenticati possono accedere a `/auth/**`
                .requestMatchers("/user/admin/**").hasRole("ADMIN") // 🔒 Solo gli admin possono accedere a `/admin/**`
                .anyRequest().authenticated() // 🔒 Tutte le altre richieste richiedono autenticazione
        );

        // Configuriamo il meccanismo di autenticazione stateless
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Aggiungiamo il nostro filtro JWT prima del filtro di autenticazione di Spring Security
        httpSecurity.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
