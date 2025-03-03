package com.example.CapstoneFinalProjectBE.payload;

import com.example.CapstoneFinalProjectBE.model.Evento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UtenteDTO {

    private long id;

    @NotNull(message = "Il campo nome è obbligatorio")
    private String nome;

    @NotNull(message = "Il campo cognome è obbligatorio")
    private String cognome;

    @NotNull(message = "Il campo email è obbligatorio")
    @Email
    private String email;

    private boolean isAdmin = false; // Di default FALSE è User, TRUE è ADMIN

    private List<EventoDTO> eventiPrenotati;

}
