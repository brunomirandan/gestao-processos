package com.juridico.processos.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.juridico.processos.model.UserAccount;
import com.juridico.processos.repository.UserAccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserAccountRepository userRepo;

	public CustomUserDetailsService(UserAccountRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount u = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

		var authorities = u.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName()))
				.collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), u.isEnabled(),
				true, true, true, authorities);
	}
}
