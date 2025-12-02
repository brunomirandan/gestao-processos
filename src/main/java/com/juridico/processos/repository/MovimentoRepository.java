package com.juridico.processos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juridico.processos.model.Movimento;

public interface MovimentoRepository extends JpaRepository<Movimento, Long> {
}