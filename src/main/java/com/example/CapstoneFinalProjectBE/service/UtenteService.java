package com.example.CapstoneFinalProjectBE.service;

import com.example.CapstoneFinalProjectBE.exception.EmailDuplicateException;
import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.payload.UtenteDTO;
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
    UtenteRepository utenteRepo;


    // CREAZIONE UTENTE
    public String creaUtente(UtenteDTO utenteDTO) {
        checkDuplicateKey(utenteDTO.getEmail());

        Utente utente = dtoToEntity(utenteDTO);
        utenteRepo.save(utente);

        return "Utente creato correttamente con ID: " + utente.getId();
    }

    // OTTIENI UN UTENTE PER ID
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
            UtenteDTO dto = entityToDto(utente);
            listaUtentiDTO.add(dto);
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



    // CONTROLLO DUPLICAZIONE EMAIL
    public void checkDuplicateKey(String email) throws EmailDuplicateException {
        if (utenteRepo.existsByEmail(email)) {
            throw new EmailDuplicateException("Email già utilizzata da un altro utente");
        }
    }

// TRAVASO DTO → ENTITY
private Utente dtoToEntity(UtenteDTO dto) {
    Utente utente = new Utente();
    utente.setNome(dto.getNome());
    utente.setCognome(dto.getCognome());
    utente.setEmail(dto.getEmail());
    utente.setAdmin(dto.isAdmin());
    return utente;
}

// TRAVASO ENTITY → DTO
private UtenteDTO entityToDto(Utente utente) {
    UtenteDTO dto = new UtenteDTO();
    dto.setId(utente.getId());
    dto.setNome(utente.getNome());
    dto.setCognome(utente.getCognome());
    dto.setEmail(utente.getEmail());
    dto.setAdmin(utente.isAdmin());
    return dto;
}
}

