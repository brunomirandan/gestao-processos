package com.juridico.processos.service;

import com.juridico.processos.model.Parte;
import com.juridico.processos.repository.ParteRepository;
import com.juridico.processos.util.CpfCnpjUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ParteService {

	private final ParteRepository parteRepository;

	public ParteService(ParteRepository parteRepository) {
		this.parteRepository = parteRepository;
	}

	public Page<Parte> listar(String nome, String identificacao, Pageable pageable) {
		boolean temNome = nome != null && !nome.isBlank();
		boolean temIdent = identificacao != null && !identificacao.isBlank();

		if (temNome && temIdent) {
			return parteRepository.findByNomeContainingIgnoreCaseAndIdentificacaoContaining(nome, identificacao,
					pageable);
		} else if (temNome) {
			return parteRepository.findByNomeContainingIgnoreCase(nome, pageable);
		} else if (temIdent) {
			return parteRepository.findByIdentificacaoContaining(identificacao, pageable);
		} else {
			return parteRepository.findAll(pageable);
		}
	}

	public Parte buscarPorId(Long id) {
		return parteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Parte não encontrada."));
	}

	public Parte salvar(Parte parte) {
		String identNormalizada = CpfCnpjUtils.somenteDigitos(parte.getIdentificacao());
		parte.setIdentificacao(identNormalizada);

		if (!CpfCnpjUtils.isValidCpfOrCnpj(identNormalizada)) {
			throw new IllegalArgumentException("Identificação inválida. Informe um CPF ou CNPJ válido.");
		}

		parteRepository.findByIdentificacao(identNormalizada).ifPresent(existente -> {
			if (parte.getId() == null || !existente.getId().equals(parte.getId())) {
				throw new IllegalArgumentException("Já existe uma parte cadastrada com essa identificação.");
			}
		});

		return parteRepository.save(parte);
	}

	public void excluir(Long id) {
		parteRepository.deleteById(id);
	}
}
