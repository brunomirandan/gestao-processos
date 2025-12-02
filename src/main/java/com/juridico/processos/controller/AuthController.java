package com.juridico.processos.controller;

import java.util.HashSet;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juridico.processos.dto.auth.AuthRequest;
import com.juridico.processos.dto.auth.AuthResponse;
import com.juridico.processos.dto.auth.RegisterRequest;
import com.juridico.processos.model.Role;
import com.juridico.processos.model.UserAccount;
import com.juridico.processos.repository.RoleRepository;
import com.juridico.processos.repository.UserAccountRepository;
import com.juridico.processos.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class AuthController {

	private final AuthenticationManager authManager;
	private final JwtUtil jwtUtil;
	private final UserAccountRepository userRepo;
	private final RoleRepository roleRepo;
	private final PasswordEncoder encoder;

	public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserAccountRepository userRepo,
			RoleRepository roleRepo, PasswordEncoder encoder) {
		this.authManager = authManager;
		this.jwtUtil = jwtUtil;
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.encoder = encoder;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
		Authentication auth = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
		UserDetails user = (UserDetails) auth.getPrincipal();
		String token = jwtUtil.generateToken(user);
		List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), roles));
	}

	// opcional: pode proteger com @PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
		if (userRepo.findByUsername(req.username()).isPresent()) {
			return ResponseEntity.badRequest().body("Usuário já existe");
		}

		UserAccount u = new UserAccount();
		u.setUsername(req.username());
		u.setPassword(encoder.encode(req.password()));
		u.setEnabled(true);

		var roles = new HashSet<Role>();
		for (String nome : req.roles()) {
			Role r = roleRepo.findByName(nome).orElseGet(() -> roleRepo.save(new Role(null, nome)));
			roles.add(r);
		}
		u.setRoles(roles);

		userRepo.save(u);
		return ResponseEntity.ok("Usuário criado com sucesso");
	}
}
