package com.juridico.processos.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juridico.processos.enums.DatajudEndpoint;
import com.juridico.processos.model.Parte;
import com.juridico.processos.model.Processo;
import com.juridico.processos.repository.ParteRepository;
import com.juridico.processos.repository.ProcessoRepository;
import com.juridico.processos.repository.dto.TribunalQuantidadeDTO;

@Service
public class ProcessoService {

	private final ProcessoRepository processoRepository;
	private final ParteRepository parteRepository;
	private final DatajudImportService datajudImportService;

	public ProcessoService(ProcessoRepository processoRepository, ParteRepository parteRepository,
			DatajudImportService datajudImportService) {
		this.processoRepository = processoRepository;
		this.parteRepository = parteRepository;
		this.datajudImportService = datajudImportService;
	}

	public List<Processo> listarTodos() {
		return processoRepository.findAll();
	}

	public Optional<Processo> buscarPorId(Long id) {
		return processoRepository.findById(id);
	}

	@Transactional
	public Processo salvar(Processo processo) {

		if (processo.getJuizado() == null) {
			throw new IllegalArgumentException("O juizado do processo deve ser informado.");
		}

		if (processo.getNumeroProcesso() == null || processo.getNumeroProcesso().isBlank()) {
			throw new IllegalArgumentException("O número do processo deve ser informado.");
		}

		// Garantir que Autores e Réus são entidades gerenciadas (buscar pelo ID)
		Set<Parte> autores = carregarPartes(processo.getAutores());
		Set<Parte> reus = carregarPartes(processo.getReus());

		validarPartes(autores, reus);

		processo.setAutores(autores);
		processo.setReus(reus);

		Processo salvo = processoRepository.save(processo);

		// Importação automática usando o enum (link) + número do processo
		importarAndamentosAutomaticamente(salvo.getJuizado(), salvo.getNumeroProcesso());

		return salvo;
	}

	@Transactional
	public void excluir(Long id) {
		processoRepository.deleteById(id);
	}

	private Set<Parte> carregarPartes(Set<Parte> partes) {
		if (partes == null || partes.isEmpty())
			return new LinkedHashSet<>();

		return partes.stream().filter(Objects::nonNull).map(p -> {
			if (p.getId() == null) {
				throw new IllegalArgumentException("Parte sem ID informado.");
			}
			return parteRepository.findById(p.getId())
					.orElseThrow(() -> new IllegalArgumentException("Parte com ID " + p.getId() + " não encontrada."));
		}).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private void validarPartes(Set<Parte> autores, Set<Parte> reus) {

		List<Long> autoresIds = autores.stream().map(Parte::getId).toList();

		List<Long> reusIds = reus.stream().map(Parte::getId).toList();

		// repetidos dentro da própria lista
		if (new HashSet<>(autoresIds).size() != autoresIds.size()) {
			throw new IllegalArgumentException("A lista de autores contém registros repetidos.");
		}

		if (new HashSet<>(reusIds).size() != reusIds.size()) {
			throw new IllegalArgumentException("A lista de réus contém registros repetidos.");
		}

		// interseção autor/réu
		Set<Long> intersecao = new HashSet<>(autoresIds);
		intersecao.retainAll(reusIds);

		if (!intersecao.isEmpty()) {
			throw new IllegalArgumentException("Uma mesma parte não pode ser Autor e Réu no mesmo processo.");
		}
	}

	private void importarAndamentosAutomaticamente(DatajudEndpoint endpoint, String numeroProcesso) {
		try {
			datajudImportService.importarProcessoCompleto(endpoint, numeroProcesso);
		} catch (Exception e) {
			// aqui você pode logar o erro, mas não impede o salvamento do processo
			// ex: log.warn("Falha ao importar andamentos", e);
		}
	}

	public List<TribunalQuantidadeDTO> relatorioProcessosPorTribunal() {
		return processoRepository.contarProcessosPorTribunal();
	}

}
