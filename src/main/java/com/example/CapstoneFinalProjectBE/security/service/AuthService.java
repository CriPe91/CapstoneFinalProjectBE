package com.example.CapstoneFinalProjectBE.security.service;


import com.example.CapstoneFinalProjectBE.exception.EmailDuplicateException;
import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.payload.request.RegistrazioneRequest;
import com.example.CapstoneFinalProjectBE.payload.response.LoginResponse;
import com.example.CapstoneFinalProjectBE.repository.UtenteRepository;
import com.example.CapstoneFinalProjectBE.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthService {

    @Autowired
    private UtenteRepository utenteRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ REGISTRAZIONE UTENTE
    public String register(RegistrazioneRequest registrazione) {
        if (utenteRepo.existsByEmail(registrazione.getEmail())) {
            throw new EmailDuplicateException("Errore: L'email è già registrata!");
        }

        Utente utente = new Utente();
        utente.setNome(registrazione.getNome());
        utente.setCognome(registrazione.getCognome());
        utente.setEmail(registrazione.getEmail());
        utente.setPassword(passwordEncoder.encode(registrazione.getPassword())); //  Cripta la password
       if(registrazione.getIsAdmin() == null || registrazione.getIsAdmin().equals(false)){
           utente.setIsAdmin(false);
       }else if(registrazione.getIsAdmin().equals(true)){
           utente.setIsAdmin(true);
       }

        Long id = utenteRepo.save(utente).getId();
        return "Registrazione completata con successo! ID Utente: " + id;
    }

    // ✅ NUOVO METODO: REGISTRA UTENTE E RESTITUISCE L'OGGETTO UTENTE CON TOKEN PER IL FE
    public Utente registerAndReturnUser(RegistrazioneRequest registrazione) {
        if (utenteRepo.existsByEmail(registrazione.getEmail())) {
            throw new EmailDuplicateException("Errore: L'email è già registrata!");
        }

        Utente utente = new Utente();
        utente.setNome(registrazione.getNome());
        utente.setCognome(registrazione.getCognome());
        utente.setEmail(registrazione.getEmail());
        utente.setPassword(passwordEncoder.encode(registrazione.getPassword())); // Cripta la password
        utente.setIsAdmin(registrazione.getIsAdmin() != null && registrazione.getIsAdmin());

        return utenteRepo.save(utente); // Ora ritorniamo l'utente appena registrato
    }

    // ✅ LOGIN UTENTE
    public LoginResponse login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Recuperiamo l'utente dal database
            Utente user = utenteRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Errore: Nessun utente trovato con questa email."));

            // Generiamo il token JWT
            String token = jwtUtil.creaToken(user);
            return new LoginResponse(email, token);

        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Errore: Credenziali non valide. Riprova.");
        }
    }

}
