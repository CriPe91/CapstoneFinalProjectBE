package com.example.CapstoneFinalProjectBE.service;

import com.example.CapstoneFinalProjectBE.exception.EmailDuplicateException;
import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.payload.UtenteDTO;
import com.example.CapstoneFinalProjectBE.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepo;

    // OTTIENI UN UTENTE PER ID (SOLO ADMIN)
    @PreAuthorize("hasRole('ADMIN')") // Solo gli Admin possono cercare utenti per ID
    public UtenteDTO getUtenteById(Long id) {
        Utente utente = utenteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
        return entityToDto(utente);
    }

    // OTTIENI TUTTI GLI UTENTI CON PAGINAZIONE (SOLO ADMIN)
    @PreAuthorize("hasRole('ADMIN')") // Solo gli Admin possono visualizzare tutti gli utenti
    public Page<UtenteDTO> getAllUtenti(Pageable pageable) {
        Page<Utente> listaUtenti = utenteRepo.findAll(pageable);
        List<UtenteDTO> listaUtentiDTO = new ArrayList<>();

        for (Utente utente : listaUtenti.getContent()) {
            listaUtentiDTO.add(entityToDto(utente));
        }

        return new PageImpl<>(listaUtentiDTO, pageable, listaUtenti.getTotalElements());
    }

    // ELIMINA UN UTENTE (SOLO ADMIN)
    @PreAuthorize("hasRole('ADMIN')") // Solo gli Admin possono eliminare utenti
    public String deleteUtente(Long id) {
        Utente utente = utenteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
        utenteRepo.delete(utente);
        return "Utente con ID: " + id + " eliminato con successo.";
    }

    // TRAVASO ENTITY → DTO
    private UtenteDTO entityToDto(Utente utente) {
        UtenteDTO dto = new UtenteDTO();
        dto.setId(utente.getId());
        dto.setNome(utente.getNome());
        dto.setCognome(utente.getCognome());
        dto.setEmail(utente.getEmail());
        dto.setIsAdmin(utente.getIsAdmin());
        return dto;
    }
}

