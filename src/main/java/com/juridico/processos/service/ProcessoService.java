package com.juridico.processos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juridico.processos.model.Processo;
import com.juridico.processos.repository.ProcessoRepository;

@Service
public class ProcessoService {

	private final ProcessoRepository repo;

	public ProcessoService(ProcessoRepository repo) {
		this.repo = repo;
	}

	@Transactional
	public Processo salvar(Processo p) {
		// garantir vínculo bidirecional
		if (p.getMovimentos() != null) {
			p.getMovimentos().forEach(m -> {
				m.setProcesso(p);
				if (m.getComplementosTabelados() != null) {
					m.getComplementosTabelados().forEach(c -> c.setMovimento(m));
				}
			});
		}
		if (p.getAssuntos() != null) {
			p.getAssuntos().forEach(a -> a.setProcesso(p));
		}
		return repo.save(p);
	}
}