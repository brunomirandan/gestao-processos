package com.juridico.processos.dto.user;

import java.util.List;

public record UserUpdateDTO(boolean enabled, List<String> roles) {
}
