package com.juridico.processos.dto.user;

import java.util.List;

public record UserCreateDTO(String username, String password, List<String> roles) {
}
