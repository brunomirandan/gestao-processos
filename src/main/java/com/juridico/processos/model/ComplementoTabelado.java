package com.juridico.processos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "complementoTabelado")
public class ComplementoTabelado {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Integer codigo;
	private Integer valor;
	private String nome;
	private String descricao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movimento_id")
	private Movimento movimento;

	public Long getId() {
		return id;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public Integer getValor() {
		return valor;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public Movimento getMovimento() {
		return movimento;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setMovimento(Movimento movimento) {
		this.movimento = movimento;
	}

}