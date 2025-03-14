package com.example.CapstoneFinalProjectBE.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtenteSenzaEventiDTO {
    private long id;
    private String nome;
    private String cognome;
    private String email;
    private Boolean isAdmin;
}
