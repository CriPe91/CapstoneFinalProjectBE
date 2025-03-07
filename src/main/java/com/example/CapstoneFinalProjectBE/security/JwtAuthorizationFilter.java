package com.example.CapstoneFinalProjectBE.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Ignora il filtro per le richieste di registrazione e login
            String path = request.getServletPath();
            if (path.equals("/user/register") || path.equals("/user/login")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Recupera il token dalla richiesta
            String token = jwtUtil.recuperoToken(request);
            if (token == null || token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            // Decodifica e valida il token
            Claims claims = jwtUtil.validaClaims(request);
            System.out.println("Claims estratti dal JWT: " + claims);

            //  Controllo dei ruoli
            if (claims != null && jwtUtil.checkExpiration(claims)) {
                // Controlla se il claim "roles" è presente
                String ruolo = claims.get("roles", String.class);

                // Se il ruolo non è specificato o non è valido, assegniamo "ROLE_USER" di default
                if (ruolo == null || (!ruolo.equals("ROLE_USER") && !ruolo.equals("ROLE_ADMIN"))) {
                    ruolo = "ROLE_USER";
                }

                // Debug per vedere il ruolo assegnato
                System.out.println("Ruolo assegnato a Spring Security: " + ruolo);

                // Creiamo la lista di autorizzazioni per Spring Security
                List<SimpleGrantedAuthority> ruoli = List.of(new SimpleGrantedAuthority(ruolo));


                // Creiamo il token di autenticazione con email e ruoli
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.get("email"), "", ruoli);


                // Impostiamo l'autenticazione nel Security Context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

        } catch (Exception e) {
            // Se il token è invalido, restituiamo errore con messaggio dettagliato
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Autenticazione negata");
            errorDetails.put("details", e.getMessage());
            new ObjectMapper().writeValue(response.getWriter(), errorDetails);
            return;
        }

        // Continua con il filtro successivo
        filterChain.doFilter(request, response);
    }
}
