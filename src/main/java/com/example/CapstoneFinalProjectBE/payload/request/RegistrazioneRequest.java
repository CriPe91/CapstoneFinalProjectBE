package com.example.CapstoneFinalProjectBE.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrazioneRequest {

    @NotBlank(message = "Nome è un campo obbligatorio")
    private String nome;

    @NotBlank(message = "Cognome è un campo obbligatorio")
    private String cognome;

    @NotBlank(message = "Email è un campo obbligatorio")
    @Email(message = "Il formato email inserito non è valido")
    private String email;

    @NotBlank(message = "Password è un campo obbligatorio")
    @Size(min = 3, max = 20)
    private String password;


    private Boolean isAdmin; // Per default l'utente non è admin

}
