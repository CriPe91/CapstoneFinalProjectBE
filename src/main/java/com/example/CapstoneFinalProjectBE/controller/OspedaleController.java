package com.example.CapstoneFinalProjectBE.controller;

import com.example.CapstoneFinalProjectBE.payload.OspedaleDTO;
import com.example.CapstoneFinalProjectBE.service.OspedaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ospedali")
public class OspedaleController {

    @Autowired
    private OspedaleService ospedaleService;

    // CREAZIONE OSPEDALE (Con supporto immagine)
    @PostMapping("/newOspedale")
    public ResponseEntity<?> creaOspedale(@RequestPart("dati") @Validated OspedaleDTO dto,
                                          @RequestPart(value = "imgOspedale", required = false) MultipartFile imgOspedale) {
        try {
            String messaggio = ospedaleService.creaOspedale(dto, imgOspedale);
            return new ResponseEntity<>(messaggio, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Errore durante l'upload dell'immagine: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // CERCA OSPEDALE PER NOME CON QUERY PARAM
    @GetMapping("/search")
    public ResponseEntity<OspedaleDTO> getOspedaleByNome(@RequestParam String nome) {
        return ResponseEntity.ok(ospedaleService.findByNome(nome));
    }

    // CERCA TUTTI GLI OSPEDALI SENZA EVENTI AL SUO INTERNO
    @GetMapping("/withoutEventi")
    public ResponseEntity<List<OspedaleDTO>> getOspedaliWithoutEventi() {
        return ResponseEntity.ok(ospedaleService.findAllOspedaliWithoutEventi());
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

    // OTTIENI TUTTI GLI OSPEDALI CON EVENTO AL SUO INTERNO
    @GetMapping
    public ResponseEntity<Page<OspedaleDTO>> getAllOspedali(Pageable pageable) {
        Page<OspedaleDTO> ospedali = ospedaleService.getAllOspedali(pageable);
        return new ResponseEntity<>(ospedali, HttpStatus.OK);
    }

    // MODIFICA OSPEDALE (Modifica solo i campi presenti nel JSON)
    @PutMapping("/{id}")
    public ResponseEntity<?> modificaOspedale(@PathVariable Long id, @RequestBody OspedaleDTO dto) {
        String messaggio = ospedaleService.modificaOspedale(id, dto);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

    // ELIMINA OSPEDALE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOspedale(@PathVariable Long id) {
        String messaggio = ospedaleService.deleteOspedale(id);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

}
