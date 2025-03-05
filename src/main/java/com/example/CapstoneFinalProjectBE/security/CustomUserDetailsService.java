package com.example.CapstoneFinalProjectBE.security;

import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //  Recuperiamo l'utente dal DB usando l'email
        Utente user = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        return User.builder()
                .username(user.getEmail()) //  Email usata come identificativo
                .password(user.getPassword())
                .roles(user.getIsAdmin() ? "ADMIN" : "USER") //  Ruolo basato su isAdmin
                .build();
    }

}
