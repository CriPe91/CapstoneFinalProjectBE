package com.example.CapstoneFinalProjectBE.controller;

import com.example.CapstoneFinalProjectBE.exception.ResourceNotFoundException;
import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.service.EventoService;
import com.example.CapstoneFinalProjectBE.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private UtenteService utenteService;

    // CREAZIONE EVENTO (Con immagine)
    @PostMapping("/newEvento")
    public ResponseEntity<?> creaEvento(@RequestPart("dati") @Validated EventoDTO dto,
                                        @RequestPart(value = "imgEvento", required = false) MultipartFile imgEvento) {
        try {
            String messaggio = eventoService.creaEvento(dto, imgEvento);
            return new ResponseEntity<>(messaggio, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante l'upload dell'immagine: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // CERCA EVENTO PER TITOLO DATA O ENTRAMBI CON QUERY PARAM
    @GetMapping("/search")
    public ResponseEntity<?> searchEventi(
            @RequestParam(required = false) String titolo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            List<EventoDTO> eventi = eventoService.findByTitoloOrData(titolo, data);
            return ResponseEntity.ok(eventi);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // OTTIENI UN EVENTO PER ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventoById(@PathVariable Long id) {
        try {
            EventoDTO evento = eventoService.getEventoById(id);
            return new ResponseEntity<>(evento, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Errore: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // OTTIENI TUTTI GLI EVENTI
    @GetMapping
    public ResponseEntity<Page<EventoDTO>> getAllEventi(Pageable pageable) {
        Page<EventoDTO> eventi = eventoService.getAllEventi(pageable);
        return new ResponseEntity<>(eventi, HttpStatus.OK);
    }

    // MODIFICA EVENTO (Modifica solo i campi presenti nel JSON)
    @PutMapping("/{id}")
    public ResponseEntity<?> modificaEvento(@PathVariable Long id, @RequestBody EventoDTO dto) {
        String messaggio = eventoService.modificaEvento(id, dto);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

    // ELIMINA EVENTO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(@PathVariable Long id) {
        String messaggio = eventoService.deleteEvento(id);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

    // PRENOTAZIONE UTENTE AD UN EVENTO (Solo utenti normali, no admin)
    @PostMapping("/{eventoId}/prenota/{utenteId}")
    public ResponseEntity<?> prenotaUtente(@PathVariable Long eventoId, @PathVariable Long utenteId) {
        // Recupera l'utente dal database
        Utente utente = utenteService.findUtenteById(utenteId); // FIX , ABBIAMO IL METODO IN UTENTE SERVICE

        if (utente == null) {
            return new ResponseEntity<>("Utente non trovato", HttpStatus.NOT_FOUND);
        }

        // Verifica che non sia un admin
        if (utente.getIsAdmin()) {
            return new ResponseEntity<>("Gli admin non possono prenotarsi agli eventi", HttpStatus.FORBIDDEN);
        }

        // Effettua la prenotazione
        String messaggio = eventoService.prenotaUtente(eventoId, utenteId);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

    // OTTENERE GLI EVENTI A CUI L'UTENTE È PRENOTATO
    @GetMapping("/prenotati/{utenteId}")
    public ResponseEntity<Page<EventoDTO>> getEventiPrenotati(@PathVariable Long utenteId, Pageable pageable) {
        Page<EventoDTO> eventi = eventoService.getEventiPrenotati(utenteId, pageable);
        return new ResponseEntity<>(eventi, HttpStatus.OK);
    }

    // ANNULLARE UNA PRENOTAZIONE AD UN EVENTO
    @DeleteMapping("/{eventoId}/annulla/{utenteId}")
    public ResponseEntity<?> cancellaPrenotazione(@PathVariable Long eventoId, @PathVariable Long utenteId) {
        String messaggio = eventoService.cancellaPrenotazione(eventoId, utenteId);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }
}
