package com.example.CapstoneFinalProjectBE.repository;

import com.example.CapstoneFinalProjectBE.model.Ospedale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OspedaleRepository extends JpaRepository<Ospedale,Long> {
    boolean existsByNome(String nome);

    // CERCA OSPEDALE PER NOME CON LISTA EVENTI AL SUO INTERNO
    Optional<Ospedale> findByNome(String nome);

    // CERCA LISTA DI OSPEDALI SENZA EVENTI ALL INTERNO

    List<Ospedale> findAll();
}
