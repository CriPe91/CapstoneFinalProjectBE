package com.example.CapstoneFinalProjectBE.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    /**
     *  Bean per la codifica delle password con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     *  Configura l'AuthenticationManager per gestire l'autenticazione
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

        //  Disabilitiamo CORS e CSRF perché useremo JWT (stateless)
        httpSecurity.cors(c -> c.configurationSource(cors -> {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("http://localhost:5173"); //URL del frontend
            config.addAllowedMethod("*"); // Consente tutti i metodi
            config.addAllowedHeader("*"); // Consente tutte le intestazioni
            return config;
        }));
        httpSecurity.csrf(csrf -> csrf.disable());

        //  Configuriamo i permessi di accesso alle API
        httpSecurity.authorizeHttpRequests(auth -> auth
                //  Permettiamo la registrazione e il login senza autenticazione
                .requestMatchers("/user/register", "/user/login").permitAll()

                //  Permettiamo a tutti di visualizzare gli ospedali e gli eventi
                .requestMatchers(HttpMethod.GET, "/ospedali").permitAll()
                .requestMatchers(HttpMethod.GET, "/ospedali/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/eventi/**").permitAll()


                // RICERCA PER QUERY PARAMS
                .requestMatchers(HttpMethod.GET, "/ospedali/search").permitAll()
                // RICERCA PER QUERY PARAMS
                .requestMatchers(HttpMethod.GET, "/eventi/search").permitAll()


                //  Solo utenti autenticati possono prenotarsi e cancellarsi agli eventi
                .requestMatchers(HttpMethod.POST, "/eventi/*/prenota/*").hasAuthority("ROLE_USER")
                .requestMatchers(HttpMethod.DELETE, "/eventi/*/annulla/*").hasAuthority("ROLE_USER")

                //  Utenti che vedono le loro prenotazioni
                .requestMatchers(HttpMethod.GET, "/eventi/prenotati/{utenteId}").hasAuthority("ROLE_USER")

                //  Solo gli admin possono creare, modificare o eliminare ospedali ed eventi
                .requestMatchers(HttpMethod.POST, "/ospedali/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/ospedali/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/ospedali/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/eventi/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/eventi/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/eventi/**").hasAuthority("ROLE_ADMIN")


                //  Qualsiasi altra richiesta richiede autenticazione
                .anyRequest().authenticated()
        );

        //  Configuriamo il meccanismo di autenticazione stateless
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //  Aggiungiamo il filtro JWT prima del filtro di autenticazione standard di Spring Security
        httpSecurity.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
