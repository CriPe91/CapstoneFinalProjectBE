package com.example.CapstoneFinalProjectBE.repository;

import com.example.CapstoneFinalProjectBE.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente,Long> {

    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);
}
