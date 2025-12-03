package com.juridico.processos.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Classe {

	private Integer codigoClasse;
	private String nomeClasse;

	public Integer getCodigoClasse() {
		return codigoClasse;
	}

	public String getNomeClasse() {
		return nomeClasse;
	}

	public void setCodigoClasse(Integer codigoClasse) {
		this.codigoClasse = codigoClasse;
	}

	public void setNomeClasse(String nomeClasse) {
		this.nomeClasse = nomeClasse;
	}

}