package com.example.CapstoneFinalProjectBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "eventi")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Evento {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long id;

private String titolo;

private String descrizione;

private LocalDate data;

@ManyToOne
@JoinColumn(name = "ospedale_id")
private Ospedale ospedale;

@ManyToMany
@JoinTable(name = "evento_utenti",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "utente_id")
            )
private List<Utente> utenti;

}
