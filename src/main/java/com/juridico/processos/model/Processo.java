package com.juridico.processos.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Processo {

	@Id
	private String id; // ex: TRF1_436_JE_16403_00008323520184013202

	@NotBlank
	private String numeroProcesso;

	private String tribunal; // TRF1
	private String grau; // JE

	@Embedded
	private ClasseInfo classe;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "codigo", column = @Column(name = "sistema_codigo")),
			@AttributeOverride(name = "nome", column = @Column(name = "sistema_nome")) })
	private SistemaInfo sistema;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "codigo", column = @Column(name = "formato_codigo")),
			@AttributeOverride(name = "nome", column = @Column(name = "formato_nome")) })
	private FormatoInfo formato;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "codigoMunicipioIBGE", column = @Column(name = "orgao_codigo_municipio_ibge")),
			@AttributeOverride(name = "codigo", column = @Column(name = "orgao_codigo")),
			@AttributeOverride(name = "nome", column = @Column(name = "orgao_nome")) })
	private OrgaoJulgador orgaoJulgador;

	private Integer nivelSigilo;

	private Instant dataHoraUltimaAtualizacao;

	private Instant dataAjuizamento;

	@Column(name = "timestamp_index")
	private Instant timestampIndex; // mapeia @timestamp do JSON

	@OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Movimento> movimentos = new ArrayList<>();

	@OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Assunto> assuntos = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public String getTribunal() {
		return tribunal;
	}

	public void setTribunal(String tribunal) {
		this.tribunal = tribunal;
	}

	public String getGrau() {
		return grau;
	}

	public void setGrau(String grau) {
		this.grau = grau;
	}

	public ClasseInfo getClasse() {
		return classe;
	}

	public void setClasse(ClasseInfo classe) {
		this.classe = classe;
	}

	public SistemaInfo getSistema() {
		return sistema;
	}

	public void setSistema(SistemaInfo sistema) {
		this.sistema = sistema;
	}

	public FormatoInfo getFormato() {
		return formato;
	}

	public void setFormato(FormatoInfo formato) {
		this.formato = formato;
	}

	public OrgaoJulgador getOrgaoJulgador() {
		return orgaoJulgador;
	}

	public void setOrgaoJulgador(OrgaoJulgador orgaoJulgador) {
		this.orgaoJulgador = orgaoJulgador;
	}

	public Integer getNivelSigilo() {
		return nivelSigilo;
	}

	public void setNivelSigilo(Integer nivelSigilo) {
		this.nivelSigilo = nivelSigilo;
	}

	public Instant getDataHoraUltimaAtualizacao() {
		return dataHoraUltimaAtualizacao;
	}

	public void setDataHoraUltimaAtualizacao(Instant dataHoraUltimaAtualizacao) {
		this.dataHoraUltimaAtualizacao = dataHoraUltimaAtualizacao;
	}

	public Instant getDataAjuizamento() {
		return dataAjuizamento;
	}

	public void setDataAjuizamento(Instant dataAjuizamento) {
		this.dataAjuizamento = dataAjuizamento;
	}

	public Instant getTimestampIndex() {
		return timestampIndex;
	}

	public void setTimestampIndex(Instant timestampIndex) {
		this.timestampIndex = timestampIndex;
	}

	public List<Movimento> getMovimentos() {
		return movimentos;
	}

	public void setMovimentos(List<Movimento> movimentos) {
		this.movimentos = movimentos;
	}

	public List<Assunto> getAssuntos() {
		return assuntos;
	}

	public void setAssuntos(List<Assunto> assuntos) {
		this.assuntos = assuntos;
	}

}