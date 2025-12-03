package com.juridico.processos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.juridico.processos.model.Processo;
import com.juridico.processos.repository.dto.TribunalQuantidadeDTO;

public interface ProcessoRepository extends JpaRepository<Processo, Long> {

	Optional<Processo> findByNumeroProcesso(String numeroProcesso);

	@Query("SELECT p.tribunal AS tribunal, COUNT(p) AS quantidade "
			+ "FROM Processo p GROUP BY p.tribunal ORDER BY p.tribunal")
	List<TribunalQuantidadeDTO> contarProcessosPorTribunal();

}
