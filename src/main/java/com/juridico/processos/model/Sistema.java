package com.juridico.processos.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Sistema {

	private Integer codigoSistema;
	private String nomeSistema;

	public Integer getCodigoSistema() {
		return codigoSistema;
	}

	public String getNomeSistema() {
		return nomeSistema;
	}

	public void setCodigoSistema(Integer codigoSistema) {
		this.codigoSistema = codigoSistema;
	}

	public void setNomeSistema(String nomeSistema) {
		this.nomeSistema = nomeSistema;
	}

}