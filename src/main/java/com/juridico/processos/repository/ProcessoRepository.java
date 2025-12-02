package com.juridico.processos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juridico.processos.model.Processo;

public interface ProcessoRepository extends JpaRepository<Processo, String> {
}