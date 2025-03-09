package com.example.CapstoneFinalProjectBE.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
@NoArgsConstructor  // Aggiunto per evitare errori nei travasi
@JsonInclude(JsonInclude.Include.NON_NULL) // Esclude i campi nulli dal JSON
public class OspedaleDTO {

    private long id;

    @NotNull(message = "Il campo nome è obbligatorio")
    private String nome;

    @NotNull(message = "Il campo indirizzo è obbligatorio")
    private String indirizzo;

    @NotNull(message = "Il campo email è obbligatorio")
    @Email
    private String email;

    @URL(protocol = "https")
    private String imgOspedale;

    private List<EventoDTO> eventi; // Se null, verrà escluso dal JSON


}
