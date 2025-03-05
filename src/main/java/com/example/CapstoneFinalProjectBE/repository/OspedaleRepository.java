package com.example.CapstoneFinalProjectBE.repository;

import com.example.CapstoneFinalProjectBE.model.Ospedale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OspedaleRepository extends JpaRepository<Ospedale,Long> {
    boolean existsByNome(String nome);
}
