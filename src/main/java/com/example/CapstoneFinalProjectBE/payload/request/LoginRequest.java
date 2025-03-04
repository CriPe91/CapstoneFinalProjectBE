package com.example.CapstoneFinalProjectBE.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email è un campo obbligatorio")
    @Size(min = 5, max = 40)
    private String email; // L'utente si autentica con l'email, non con lo username

    @NotBlank(message = "Password è un campo obbligatorio")
    @Size(min = 3, max = 20)
    private String password;

}
