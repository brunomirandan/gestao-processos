package com.juridico.processos.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Formato {

	private Integer codigoFormato;
	private String nomeFormato;

	public Integer getCodigoFormato() {
		return codigoFormato;
	}

	public String getNomeFormato() {
		return nomeFormato;
	}

	public void setCodigoFormato(Integer codigoFormato) {
		this.codigoFormato = codigoFormato;
	}

	public void setNomeFormato(String nomeFormato) {
		this.nomeFormato = nomeFormato;
	}

}