package com.example.CapstoneFinalProjectBE.controller;

import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // CREAZIONE EVENTO (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/newEvento")
    public ResponseEntity<?> creaEvento(@Valid @RequestPart("dati") EventoDTO dto,
                                        @RequestPart("imgEvento") MultipartFile imgEvento) {
        try {
            String messaggio = eventoService.creaEvento(dto, imgEvento);
            return new ResponseEntity<>(messaggio, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante l'upload dell'immagine: " + e.getMessage(), HttpStatus.BAD_REQUEST);
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

    // OTTIENI TUTTI GLI EVENTI (Paginazione)
    @GetMapping
    public ResponseEntity<Page<EventoDTO>> getAllEventi(Pageable pageable) {
        Page<EventoDTO> eventi = eventoService.getAllEventi(pageable);
        return new ResponseEntity<>(eventi, HttpStatus.OK);
    }

    // MODIFICA EVENTO (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> modificaEvento(@PathVariable Long id,
                                            @Valid @RequestPart("dati") EventoDTO dto,
                                            @RequestPart("imgEvento") MultipartFile imgEvento) {
        try {
            String messaggio = eventoService.modificaEvento(id, dto, imgEvento);
            return new ResponseEntity<>(messaggio, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante l'upload dell'immagine: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ELIMINA EVENTO (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(@PathVariable Long id) {
        try {
            String messaggio = eventoService.deleteEvento(id);
            return new ResponseEntity<>(messaggio, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Errore: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
