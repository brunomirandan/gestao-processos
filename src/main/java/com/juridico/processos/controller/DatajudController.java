package com.juridico.processos.controller;

import com.juridico.processos.enums.DatajudEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DatajudController {

    @GetMapping("/juizados")
    public List<Map<String, String>> listarJuizados() {
        return Arrays.stream(DatajudEndpoint.values())
                .map(e -> Map.of(
                        "nome", e.name(),
                        "descricao", e.getDescricao()
                ))
                .toList();
    }
}
