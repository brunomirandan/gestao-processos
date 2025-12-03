package com.juridico.processos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.juridico.processos.model.Processo;
import com.juridico.processos.repository.ProcessoRepository;
import com.juridico.processos.repository.dto.TribunalQuantidadeDTO;
import com.juridico.processos.service.DatajudImportService;
import com.juridico.processos.service.ImportarExcelProcessosService;
import com.juridico.processos.service.ProcessoService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/processos")
public class ProcessoController {

	@Autowired
	private ProcessoRepository repo;

	@Autowired
	private ProcessoService service;

	@Autowired
	private DatajudImportService datajudImportService;

	@Autowired
	private ImportarExcelProcessosService importarExcelProcessosService;

	public ProcessoController(ProcessoRepository repo, ProcessoService service) {
		this.repo = repo;
		this.service = service;
	}

	@GetMapping
	public Page<Processo> listar(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "dataHoraUltimaAtualizacao") String sort,
			@RequestParam(defaultValue = "DESC") Sort.Direction dir) {
		return repo.findAll(PageRequest.of(page, size, Sort.by(dir, sort)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Processo> buscar(@PathVariable Long id) {
		return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Processo> criar(@Valid @RequestBody Processo p) {
		Processo salvo = service.salvar(p);
		return ResponseEntity.created(URI.create("/api/processos/" + salvo.getId())).body(salvo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Processo> atualizar(@PathVariable Long id, @Valid @RequestBody Processo p) {

		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		p.setId(id);
		Processo salvo = service.salvar(p);
		return ResponseEntity.ok(salvo);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repo.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/importar")
	public ResponseEntity<?> importarProcessos(@RequestParam("file") MultipartFile file) {
		List<String> erros = importarExcelProcessosService.importarExcel(file);

		if (erros.isEmpty()) {
			return ResponseEntity.ok("Todos os processos foram importados com sucesso.");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Importação finalizada.\n\n");

		sb.append("Alguns processos não foram importados:\n");
		erros.forEach(e -> sb.append(" - ").append(e).append("\n"));

		return ResponseEntity.badRequest().body(sb.toString());
	}

	@PostMapping("/{id}/importar-andamentos")
	public ResponseEntity<String> importarAndamentos(@PathVariable Processo processo) {
		String resultado = datajudImportService.importarProcessoCompleto(processo.getJuizado(),
				processo.getNumeroProcesso());
		return ResponseEntity.ok(resultado);
	}

	@GetMapping("/relatorios/quantidade-por-tribunal")
	public List<TribunalQuantidadeDTO> relatorioQuantidadePorTribunal() {
		return service.relatorioProcessosPorTribunal();
	}

}