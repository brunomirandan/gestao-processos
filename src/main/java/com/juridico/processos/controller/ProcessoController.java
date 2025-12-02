package com.juridico.processos.controller;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import com.juridico.processos.service.DatajudImportService;
import com.juridico.processos.service.ProcessoService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000") // React (CRA) → 3000. Se usar Vite, troque para 5173.
@RestController
@RequestMapping("/api/processos")
public class ProcessoController {

	@Autowired
	private ProcessoRepository repo;

	@Autowired
	private ProcessoService service;

	@Autowired
	private DatajudImportService datajudImportService;

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
	public ResponseEntity<Processo> buscar(@PathVariable String id) {
		return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Processo> criar(@Valid @RequestBody Processo p) {
		if (repo.existsById(p.getId())) {
			return ResponseEntity.badRequest().build();
		}
		Processo salvo = service.salvar(p);
		return ResponseEntity.created(URI.create("/api/processos/" + salvo.getId())).body(salvo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Processo> atualizar(@PathVariable String id, @Valid @RequestBody Processo p) {
		if (!repo.existsById(id))
			return ResponseEntity.notFound().build();
		p.setId(id);
		Processo salvo = service.salvar(p);
		return ResponseEntity.ok(salvo);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable String id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repo.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/importar")
	public ResponseEntity<String> importarExcel(@RequestParam("file") MultipartFile file) {
		try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {

			Sheet sheet = workbook.getSheetAt(0);
			List<Processo> processos = new ArrayList<>();

			// Supondo que a primeira linha é o cabeçalho
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				Processo p = new Processo();
				p.setId(getString(row, 0));
				p.setNumeroProcesso(getString(row, 1));
				p.setTribunal(getString(row, 2));
				p.setGrau(getString(row, 3));
				p.setNivelSigilo((int) getNumeric(row, 4));
				processos.add(p);
			}

			repo.saveAll(processos);
			return ResponseEntity.ok("Importação concluída: " + processos.size() + " processos.");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao importar arquivo: " + e.getMessage());
		}
	}

	private String getString(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		return (cell == null) ? "" : cell.toString().trim();
	}

	private double getNumeric(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		return (cell == null) ? 0 : cell.getNumericCellValue();
	}

	@PostMapping("/{id}/importar-andamentos")
	public ResponseEntity<String> importarAndamentos(@PathVariable String id) {
		String resultado = datajudImportService.importarProcessoCompleto(id);
		return ResponseEntity.ok(resultado);
	}

}