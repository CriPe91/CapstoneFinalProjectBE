package com.example.CapstoneFinalProjectBE.controller;

import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // **CREAZIONE EVENTO (Con immagine)**
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

    // **OTTIENI UN EVENTO PER ID**
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventoById(@PathVariable Long id) {
        try {
            EventoDTO evento = eventoService.getEventoById(id);
            return new ResponseEntity<>(evento, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Errore: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // **OTTIENI TUTTI GLI EVENTI**
    @GetMapping
    public ResponseEntity<Page<EventoDTO>> getAllEventi(Pageable pageable) {
        Page<EventoDTO> eventi = eventoService.getAllEventi(pageable);
        return new ResponseEntity<>(eventi, HttpStatus.OK);
    }

    // **MODIFICA EVENTO (Modifica solo i campi presenti nel JSON)**
    @PutMapping("/{id}")
    public ResponseEntity<?> modificaEvento(@PathVariable Long id, @RequestBody EventoDTO dto) {
        String messaggio = eventoService.modificaEvento(id, dto);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

    // **ELIMINA EVENTO**
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(@PathVariable Long id) {
        String messaggio = eventoService.deleteEvento(id);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

}
