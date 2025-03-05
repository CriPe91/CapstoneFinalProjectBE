package com.example.CapstoneFinalProjectBE.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

@Data
public class EventoDTO {

    private long id;

    @NotNull(message = "Il campo titolo è obbligatorio")
    private String titolo;

    @NotNull(message = "Il campo descrizione è obbligatorio")
    private String descrizione;

    @NotNull(message = "Il campo data è obbligatorio")
    private LocalDate data;

    @URL(protocol = "https")
    private String imgEvento;

    private OspedaleDTO ospedale;

    private List<UtenteDTO> utenti;

}
