package com.example.CapstoneFinalProjectBE.repository;

import com.example.CapstoneFinalProjectBE.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento,Long> {
    boolean existsByTitolo(String titolo);

    List<Evento> findByTitoloContaining(String titolo);
    List<Evento> findByData(LocalDate data);
    List<Evento> findByTitoloContainingAndData(String titolo, LocalDate data);
}
