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
            // Recupero il token dalla request
            String token = jwtUtil.recuperoToken(request);
            Claims claims = jwtUtil.validaClaims(request);

            // Controlliamo la validità del token
            if (claims != null && jwtUtil.checkExpiration(claims)) {
                boolean isAdmin = (boolean) claims.get("isAdmin");

                // Assegniamo il ruolo in base a isAdmin
                List<SimpleGrantedAuthority> ruoli = List.of(new SimpleGrantedAuthority(isAdmin ? "ROLE_ADMIN" : "ROLE_USER"));

                // Creiamo il token di autenticazione
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.get("email"), "", ruoli);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (Exception e) {
            // Gestione errore di autenticazione
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
