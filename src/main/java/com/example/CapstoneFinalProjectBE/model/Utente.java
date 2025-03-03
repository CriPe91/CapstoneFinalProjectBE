package com.example.CapstoneFinalProjectBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "utenti")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;

    private String cognome;

    private String email;

    private String password;

    private boolean isAdmin = false; // Di default = FALSE è User normale, Se TRUE è un ADMIN

    @ManyToMany(mappedBy = "utenti")
    private List<Evento> eventiPrenotati;

}
