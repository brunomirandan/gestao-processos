package com.juridico.processos.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.juridico.processos.dto.user.*;
import com.juridico.processos.model.Role;
import com.juridico.processos.model.UserAccount;
import com.juridico.processos.repository.RoleRepository;
import com.juridico.processos.repository.UserAccountRepository;

@Service
public class UserAdminService {

    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public UserAdminService(UserAccountRepository userRepo,
                            RoleRepository roleRepo,
                            PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    // LISTAR TODOS
    public List<UserSummaryDTO> listarTodos() {
        return userRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // BUSCAR UM
    public UserSummaryDTO buscarPorId(Long id) {
        UserAccount u = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return toDTO(u);
    }

    // CRIAR
    public UserSummaryDTO criar(UserCreateDTO dto) {
        if (userRepo.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("Usuário já existe");
        }

        UserAccount u = new UserAccount();
        u.setUsername(dto.username());
        u.setPassword(encoder.encode(dto.password()));
        u.setEnabled(true);

        var roles = new HashSet<Role>();
        for (String rName : dto.roles()) {
            Role r = roleRepo.findByName(rName)
                    .orElseGet(() -> roleRepo.save(new Role(null, rName)));
            roles.add(r);
        }
        u.setRoles(roles);

        userRepo.save(u);
        return toDTO(u);
    }

    // ATUALIZAR ROLES + ENABLED
    public UserSummaryDTO atualizar(Long id, UserUpdateDTO dto) {
        UserAccount u = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        u.setEnabled(dto.enabled());

        var roles = new HashSet<Role>();
        for (String rName : dto.roles()) {
            Role r = roleRepo.findByName(rName)
                    .orElseGet(() -> roleRepo.save(new Role(null, rName)));
            roles.add(r);
        }
        u.setRoles(roles);

        userRepo.save(u);
        return toDTO(u);
    }

    // ALTERAR SENHA (reset)
    public void alterarSenha(Long id, ChangePasswordDTO dto) {
        UserAccount u = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        u.setPassword(encoder.encode(dto.newPassword()));
        userRepo.save(u);
    }

    // converter entidade -> DTO
    private UserSummaryDTO toDTO(UserAccount u) {
        List<String> roles = u.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        return new UserSummaryDTO(u.getId(), u.getUsername(), u.isEnabled(), roles);
    }
}
