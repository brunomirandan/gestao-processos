package com.juridico.processos.dto.user;

import java.util.List;

public record UserSummaryDTO(Long id, String username, boolean enabled, List<String> roles) {
}
