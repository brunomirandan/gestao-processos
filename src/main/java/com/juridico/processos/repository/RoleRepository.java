package com.juridico.processos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juridico.processos.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(String name);
}
