// AuthResponse.java
package com.juridico.processos.dto.auth;

import java.util.List;

public record AuthResponse(String token, String username, List<String> roles) {
}
