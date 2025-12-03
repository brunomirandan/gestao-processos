package com.juridico.processos.controller;

import com.juridico.processos.model.Parte;
import com.juridico.processos.service.ParteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partes")
public class ParteController {

	private final ParteService parteService;

	public ParteController(ParteService parteService) {
		this.parteService = parteService;
	}

	@GetMapping
	public Page<Parte> listar(@RequestParam(required = false) String nome,
			@RequestParam(required = false) String identificacao,
			@PageableDefault(sort = "nome", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
		return parteService.listar(nome, identificacao, pageable);
	}

	@GetMapping("/{id}")
	public Parte buscarPorId(@PathVariable Long id) {
		return parteService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<Parte> criar(@Valid @RequestBody Parte parte) {
		Parte salva = parteService.salvar(parte);
		return ResponseEntity.status(HttpStatus.CREATED).body(salva);
	}

	@PutMapping("/{id}")
	public Parte atualizar(@PathVariable Long id, @Valid @RequestBody Parte parte) {
		Parte existente = parteService.buscarPorId(id);
		existente.setNome(parte.getNome());
		existente.setIdentificacao(parte.getIdentificacao());
		return parteService.salvar(existente);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		parteService.excluir(id);
	}
}
