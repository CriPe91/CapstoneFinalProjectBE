package com.example.CapstoneFinalProjectBE.controller;

import com.example.CapstoneFinalProjectBE.payload.OspedaleDTO;
import com.example.CapstoneFinalProjectBE.service.OspedaleService;
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
@RequestMapping("/ospedali")
public class OspedaleController {

    @Autowired
    private OspedaleService ospedaleService;

    // CREAZIONE OSPEDALE (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/newOspedale")
    public ResponseEntity<?> creaOspedale(@Valid @RequestPart("dati") OspedaleDTO dto,
                                          @RequestPart("imgOspedale") MultipartFile imgOspedale) {
        try {
            String messaggio = ospedaleService.creaOspedale(dto, imgOspedale);
            return new ResponseEntity<>(messaggio, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante l'upload dell'immagine: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // OTTIENI UN OSPEDALE PER ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOspedaleById(@PathVariable Long id) {
        try {
            OspedaleDTO ospedale = ospedaleService.getOspedaleById(id);
            return new ResponseEntity<>(ospedale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Errore: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // OTTIENI TUTTI GLI OSPEDALI (Paginazione)
    @GetMapping
    public ResponseEntity<Page<OspedaleDTO>> getAllOspedali(Pageable pageable) {
        Page<OspedaleDTO> ospedali = ospedaleService.getAllOspedali(pageable);
        return new ResponseEntity<>(ospedali, HttpStatus.OK);
    }

    // MODIFICA OSPEDALE (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> modificaOspedale(@PathVariable Long id,
                                              @Valid @RequestPart("dati") OspedaleDTO dto,
                                              @RequestPart("imgOspedale") MultipartFile imgOspedale) {
        try {
            String messaggio = ospedaleService.modificaOspedale(id, dto, imgOspedale);
            return new ResponseEntity<>(messaggio, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante l'upload dell'immagine: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ELIMINA OSPEDALE (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOspedale(@PathVariable Long id) {
        try {
            String messaggio = ospedaleService.deleteOspedale(id);
            return new ResponseEntity<>(messaggio, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Errore: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
