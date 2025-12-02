package com.juridico.processos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juridico.processos.dto.user.ChangePasswordDTO;
import com.juridico.processos.dto.user.UserCreateDTO;
import com.juridico.processos.dto.user.UserSummaryDTO;
import com.juridico.processos.dto.user.UserUpdateDTO;
import com.juridico.processos.service.UserAdminService;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

	private final UserAdminService service;

	public UserAdminController(UserAdminService service) {
		this.service = service;
	}

	@GetMapping
	public List<UserSummaryDTO> listar() {
		return service.listarTodos();
	}

	@GetMapping("/{id}")
	public UserSummaryDTO buscar(@PathVariable Long id) {
		return service.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<UserSummaryDTO> criar(@RequestBody UserCreateDTO dto) {
		return ResponseEntity.ok(service.criar(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserSummaryDTO> atualizar(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
		return ResponseEntity.ok(service.atualizar(id, dto));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<Void> alterarSenha(@PathVariable Long id, @RequestBody ChangePasswordDTO dto) {
		service.alterarSenha(id, dto);
		return ResponseEntity.noContent().build();
	}
}
