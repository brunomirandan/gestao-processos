// RegisterRequest.java
package com.juridico.processos.dto.auth;

import java.util.List;

public record RegisterRequest(String username, String password, List<String> roles) {
}
