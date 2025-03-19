package com.example.CapstoneFinalProjectBE.service;

import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.payload.UtenteDTO;
import com.example.CapstoneFinalProjectBE.payload.response.UtenteSenzaEventiDTO;
import com.example.CapstoneFinalProjectBE.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepo;

    // NUOVO METODO: Ottiene un Utente per ID (ritorna l'entità originale)
    public Utente findUtenteById(Long id) {
        return utenteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
    }


    // CERCA UN UTENTE PER EMAIL (usato nella chiamata GET /user/me)  // PER IL FRONT-END TORNA L UTENTE AUTENTICATO
    public UtenteSenzaEventiDTO getUtenteByEmailSenzaEventi(String email) {
        Utente utente = utenteRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));
        return new UtenteSenzaEventiDTO(
                utente.getId(),
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getIsAdmin()
        );
    }

    // OTTIENI UN UTENTE PER ID (restituisce DTO)
    public UtenteDTO getUtenteById(Long id) {
        Utente utente = utenteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
        return entityToDto(utente);
    }

    // OTTIENI TUTTI GLI UTENTI CON PAGINAZIONE
    public Page<UtenteDTO> getAllUtenti(Pageable pageable) {
        Page<Utente> listaUtenti = utenteRepo.findAll(pageable);
        List<UtenteDTO> listaUtentiDTO = new ArrayList<>();

        for (Utente utente : listaUtenti.getContent()) {
            listaUtentiDTO.add(entityToDto(utente));
        }

        return new PageImpl<>(listaUtentiDTO, pageable, listaUtenti.getTotalElements());
    }

    // ELIMINA UN UTENTE
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

    // TRAVASO ENTITY → DTO SENZA EVENTI
    public UtenteSenzaEventiDTO entityToDtoSenzaEventi(Utente utente) {
        return new UtenteSenzaEventiDTO(
                utente.getId(),
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getIsAdmin()
        );
    }


}

