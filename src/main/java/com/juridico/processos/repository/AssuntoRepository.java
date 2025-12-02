package com.juridico.processos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juridico.processos.model.Assunto;

public interface AssuntoRepository extends JpaRepository<Assunto, Long> {
}