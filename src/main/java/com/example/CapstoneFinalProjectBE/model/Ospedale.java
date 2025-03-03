package com.example.CapstoneFinalProjectBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ospedali")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ospedale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;

    private String indirizzo;

    private String email;

    @OneToMany(mappedBy = "ospedale")
    private List<Evento> eventi;
}
