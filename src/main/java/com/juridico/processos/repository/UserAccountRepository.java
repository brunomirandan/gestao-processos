package com.juridico.processos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juridico.processos.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByUsername(String username);
}
