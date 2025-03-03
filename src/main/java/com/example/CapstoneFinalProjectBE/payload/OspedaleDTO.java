package com.example.CapstoneFinalProjectBE.payload;

import com.example.CapstoneFinalProjectBE.model.Evento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OspedaleDTO {

    private long id;

    @NotNull(message = "Il campo nome è obbligatorio")
    private String nome;

    @NotNull(message = "Il campo indirizzo è obbligatorio")
    private String indirizzo;

    @NotNull(message = "Il campo email è obbligatorio")
    @Email
    private String email;

    private List<EventoDTO> eventi; // Passiamo solo gli ID degli eventi associati

}
