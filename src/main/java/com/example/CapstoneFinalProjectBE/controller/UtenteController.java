package com.example.CapstoneFinalProjectBE.controller;

import com.example.CapstoneFinalProjectBE.payload.UtenteDTO;
import com.example.CapstoneFinalProjectBE.payload.request.LoginRequest;
import com.example.CapstoneFinalProjectBE.payload.request.RegistrazioneRequest;
import com.example.CapstoneFinalProjectBE.payload.response.ErroreResponseDTO;
import com.example.CapstoneFinalProjectBE.payload.response.LoginResponse;
import com.example.CapstoneFinalProjectBE.payload.response.UtenteSenzaEventiDTO;
import com.example.CapstoneFinalProjectBE.security.JwtUtil;
import com.example.CapstoneFinalProjectBE.security.service.AuthService;
import com.example.CapstoneFinalProjectBE.service.UtenteService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UtenteController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private JwtUtil jwtUtil;


    // ✅ **REGISTRAZIONE**
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrazioneRequest registrazione) {
        String response = authService.register(registrazione);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ✅ **LOGIN**
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest, BindingResult checkValidazione) {
        try {
            // Controllo della validazione dei campi
            if (checkValidazione.hasErrors()) {
                StringBuilder erroriValidazione = new StringBuilder("Problemi nella validazione:\n");
                for (ObjectError errore : checkValidazione.getAllErrors()) {
                    erroriValidazione.append("- ").append(errore.getDefaultMessage()).append("\n");
                }
                return new ResponseEntity<>(erroriValidazione.toString(), HttpStatus.BAD_REQUEST);
            }

            // Effettuiamo il login e otteniamo il token JWT
            LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

            // Restituiamo il token nel body
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Errore: Credenziali non valide", HttpStatus.BAD_REQUEST);
        }
    }


    //  OTTIENI I DATI DELL'UTENTE DAL TOKEN
    @GetMapping("/me")
    public ResponseEntity<?> getMyUserData(HttpServletRequest request) {
        try {
            Claims claims = jwtUtil.validaClaims(request);
            String email = claims.get("email", String.class);

            UtenteSenzaEventiDTO response = utenteService.getUtenteByEmailSenzaEventi(email);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new ErroreResponseDTO("Token non valido o scaduto"), HttpStatus.UNAUTHORIZED);
        }
    }




    // OTTIENI TUTTI GLI UTENTI (SOLO ADMIN)
    @GetMapping("/all")
    public ResponseEntity<Page<UtenteDTO>> getAllUtenti(Pageable pageable) {
        Page<UtenteDTO> response = utenteService.getAllUtenti(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // OTTIENI UN UTENTE PER ID (SOLO ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<UtenteDTO> getUtenteById(@PathVariable Long id) {
        UtenteDTO response = utenteService.getUtenteById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ELIMINAZIONE UTENTE (SOLO ADMIN)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUtente(@PathVariable Long id) {
        String response = utenteService.deleteUtente(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
