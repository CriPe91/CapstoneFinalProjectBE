package com.example.CapstoneFinalProjectBE.security;


import com.example.CapstoneFinalProjectBE.model.Utente;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String JWTSECRET;

    //  Durata del token in minuti (Aumentato a 59 min come richiesto)
    private final long SCADENZA = 20;

    //  Header e prefisso del token
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    //  Parser JWT per validazione
    private JwtParser JWTPARSER;

    /**
     *  Inizializzazione del parser JWT dopo la creazione del bean
     */
    @PostConstruct
    public void init() {
        JWTPARSER = Jwts.parserBuilder().setSigningKey(JWTSECRET).build();
    }

    /**
     *  Genera un token JWT per un utente autenticato
     *
     * @param utente Oggetto `Utente` con le informazioni dell'utente
     * @return Token JWT in formato stringa
     */
    public String creaToken(Utente utente) {
        Claims claims = Jwts.claims().setSubject(utente.getEmail()); // Usiamo l'email come identificativo
        claims.put("roles", utente.getIsAdmin() ? "ROLE_ADMIN" : "ROLE_USER"); //  Salviamo "ADMIN" o "USER" CON "ROLE_"
        claims.put("email", utente.getEmail());

        // Impostiamo la data di scadenza del token
        Date dataScadenza = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(SCADENZA));

        // Generazione del token JWT
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(dataScadenza)
                .signWith(SignatureAlgorithm.HS256, JWTSECRET) // Usiamo HS256 per firmare il token
                .compact();
    }

    /**
     *  Recupera il token JWT dall'header della richiesta HTTP
     *
     * @param request Richiesta HTTP
     * @return Token JWT in formato stringa (senza prefisso)
     */
    public String recuperoToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); //  Rimuove "Bearer " dal token
        }
        return null;
    }

    /**
     *  Valida il token JWT e restituisce i suoi claims
     *
     * @param request Richiesta HTTP contenente il token
     * @return Claims estratti dal token
     */
    public Claims validaClaims(HttpServletRequest request) {
        String token = recuperoToken(request);
        return JWTPARSER.parseClaimsJws(token).getBody();
    }

    /**
     *  Controlla se il token è ancora valido o è scaduto
     *
     * @param claims Claims estratti dal token JWT
     * @return `true` se il token è ancora valido, `false` se è scaduto
     */
    public boolean checkExpiration(Claims claims) {
        return claims.getExpiration().after(new Date());
    }
}
