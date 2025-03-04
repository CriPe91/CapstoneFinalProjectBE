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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Controllo se la richiesta riguarda la registrazione o il login (non richiedono token)
            String path = request.getServletPath();
            if (path.equals("/user/register") || path.equals("/user/login")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Recupero il token dalla richiesta
            String token = jwtUtil.recuperoToken(request);
            if (token == null || token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtUtil.validaClaims(request);

            // Controlliamo la validità del token
            if (claims != null && jwtUtil.checkExpiration(claims)) {
                boolean isAdmin = (boolean) claims.get("isAdmin");

                //  Assegniamo il ruolo corretto in base a isAdmin
                List<SimpleGrantedAuthority> ruoli = List.of(new SimpleGrantedAuthority(isAdmin ? "ROLE_ADMIN" : "ROLE_USER"));

                // Creiamo il token di autenticazione
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.get("email"), "", ruoli);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (Exception e) {
            // Se il token è invalido o mancante, gestiamo l'errore
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Autenticazione negata");
            errorDetails.put("details", e.getMessage());
            new ObjectMapper().writeValue(response.getWriter(), errorDetails);
        }

        filterChain.doFilter(request, response);
    }
}
