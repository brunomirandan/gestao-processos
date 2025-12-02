package com.juridico.processos.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Movimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer codigo;
	private String nome;
	private Instant dataHora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processo_id")
	private Processo processo;

	@OneToMany(mappedBy = "movimento", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComplementoTabelado> complementosTabelados = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Instant getDataHora() {
		return dataHora;
	}

	public void setDataHora(Instant dataHora) {
		this.dataHora = dataHora;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public List<ComplementoTabelado> getComplementosTabelados() {
		return complementosTabelados;
	}

	public void setComplementosTabelados(List<ComplementoTabelado> complementosTabelados) {
		this.complementosTabelados = complementosTabelados;
	}

}