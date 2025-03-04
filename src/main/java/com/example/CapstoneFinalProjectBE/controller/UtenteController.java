package com.example.CapstoneFinalProjectBE.controller;

import com.example.CapstoneFinalProjectBE.payload.UtenteDTO;
import com.example.CapstoneFinalProjectBE.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UtenteController {

    @Autowired
    UtenteService utenteService;


    //CREA UTENTE
    @PostMapping("/crea")
    public ResponseEntity<String> createUtente(@RequestBody UtenteDTO utenteDTO) {
        try {
            utenteService.creaUtente(utenteDTO);
            return new ResponseEntity<>("L'utente è stato creato con successo.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore nella creazione dell'utente: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //ELIMINA UTENTE
    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<String> eliminaUtente(@PathVariable Long id) {
        try {
            utenteService.deleteUtente(id);
            return new ResponseEntity<>("L'utente è stato eliminato con successo.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore nell'eliminazione dell'utente: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}
