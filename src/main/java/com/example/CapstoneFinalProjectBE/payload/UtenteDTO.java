package com.example.CapstoneFinalProjectBE.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore // Questa annotazione esclude la password dalle risposte JSON
    @NotNull(message = "Il campo password è obbligatorio")
    private String password;

    private Boolean isAdmin; // Di default FALSE è User, TRUE è ADMIN

    private List<EventoDTO> eventiPrenotati;

}
