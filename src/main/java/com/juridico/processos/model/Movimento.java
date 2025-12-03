package com.juridico.processos.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimento")
public class Movimento {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Integer codigo;
	private String nome;
	private Instant dataHora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processo_id")
	@JsonBackReference
	private Processo processo;

	@OneToMany(mappedBy = "movimento", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComplementoTabelado> complementosTabelados = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public String getNome() {
		return nome;
	}

	public Instant getDataHora() {
		return dataHora;
	}

	public Processo getProcesso() {
		return processo;
	}

	public List<ComplementoTabelado> getComplementosTabelados() {
		return complementosTabelados;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setDataHora(Instant dataHora) {
		this.dataHora = dataHora;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public void setComplementosTabelados(List<ComplementoTabelado> complementosTabelados) {
		this.complementosTabelados = complementosTabelados;
	}



}