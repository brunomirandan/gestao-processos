package com.juridico.processos.repository;

import com.juridico.processos.model.Parte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParteRepository extends JpaRepository<Parte, Long> {

	Optional<Parte> findByIdentificacao(String identificacao);

	Page<Parte> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

	Page<Parte> findByIdentificacaoContaining(String identificacao, Pageable pageable);

	Page<Parte> findByNomeContainingIgnoreCaseAndIdentificacaoContaining(String nome, String identificacao,
			Pageable pageable);
}
