package com.juridico.processos.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.juridico.processos.enums.DatajudEndpoint;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "processo")
public class Processo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String numeroProcesso;
	private String tribunal;
	private Instant dataHoraUltimaAtualizacao;
	private String grau;
	private Instant dataAjuizamento;
	private String idProcesso;
	private Integer nivelSigilo;

	private Classe classe;
	private Sistema sistema;
	private Formato formato;
	private OrgaoJulgador orgaoJulgador;

	@OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Movimento> movimentos = new ArrayList<Movimento>();

	@OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Assunto> assuntos = new ArrayList<Assunto>();

	@Enumerated(EnumType.STRING)
	@Column(name = "juizado", nullable = false, length = 100)
	private DatajudEndpoint juizado;

	@ManyToMany
	@JoinTable(name = "processo_autor", joinColumns = @JoinColumn(name = "processo_id"), inverseJoinColumns = @JoinColumn(name = "parte_id"))
	private Set<Parte> autores = new LinkedHashSet<>();

	@ManyToMany
	@JoinTable(name = "processo_reu", joinColumns = @JoinColumn(name = "processo_id"), inverseJoinColumns = @JoinColumn(name = "parte_id"))
	private Set<Parte> reus = new LinkedHashSet<>();

	public Processo() {
	}

	public Long getId() {
		return id;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public String getTribunal() {
		return tribunal;
	}

	public Instant getDataHoraUltimaAtualizacao() {
		return dataHoraUltimaAtualizacao;
	}

	public String getGrau() {
		return grau;
	}

	public Instant getDataAjuizamento() {
		return dataAjuizamento;
	}

	public String getIdProcesso() {
		return idProcesso;
	}

	public Integer getNivelSigilo() {
		return nivelSigilo;
	}

	public Classe getClasse() {
		return classe;
	}

	public Sistema getSistema() {
		return sistema;
	}

	public Formato getFormato() {
		return formato;
	}

	public OrgaoJulgador getOrgaoJulgador() {
		return orgaoJulgador;
	}

	public List<Movimento> getMovimentos() {
		return movimentos;
	}

	public List<Assunto> getAssuntos() {
		return assuntos;
	}

	public DatajudEndpoint getJuizado() {
		return juizado;
	}

	public Set<Parte> getAutores() {
		return autores;
	}

	public Set<Parte> getReus() {
		return reus;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public void setTribunal(String tribunal) {
		this.tribunal = tribunal;
	}

	public void setDataHoraUltimaAtualizacao(Instant dataHoraUltimaAtualizacao) {
		this.dataHoraUltimaAtualizacao = dataHoraUltimaAtualizacao;
	}

	public void setGrau(String grau) {
		this.grau = grau;
	}

	public void setDataAjuizamento(Instant dataAjuizamento) {
		this.dataAjuizamento = dataAjuizamento;
	}

	public void setIdProcesso(String idProcesso) {
		this.idProcesso = idProcesso;
	}

	public void setNivelSigilo(Integer nivelSigilo) {
		this.nivelSigilo = nivelSigilo;
	}

	public void setClasse(Classe classe) {
		this.classe = classe;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public void setFormato(Formato formato) {
		this.formato = formato;
	}

	public void setOrgaoJulgador(OrgaoJulgador orgaoJulgador) {
		this.orgaoJulgador = orgaoJulgador;
	}

	public void setMovimentos(List<Movimento> movimentos) {
		this.movimentos = movimentos;
	}

	public void setAssuntos(List<Assunto> assuntos) {
		this.assuntos = assuntos;
	}

	public void setJuizado(DatajudEndpoint juizado) {
		this.juizado = juizado;
	}

	public void setAutores(Set<Parte> autores) {
		this.autores = autores;
	}

	public void setReus(Set<Parte> reus) {
		this.reus = reus;
	}

}
