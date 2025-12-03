package com.juridico.processos.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrgaoJulgador {

	private Integer codigoMunicipioIBGE;
	private Integer codigoOrgaoJulgador;
	private String nomeOrgaoJulgador;

	public Integer getCodigoMunicipioIBGE() {
		return codigoMunicipioIBGE;
	}

	public Integer getCodigoOrgaoJulgador() {
		return codigoOrgaoJulgador;
	}

	public String getNomeOrgaoJulgador() {
		return nomeOrgaoJulgador;
	}

	public void setCodigoMunicipioIBGE(Integer codigoMunicipioIBGE) {
		this.codigoMunicipioIBGE = codigoMunicipioIBGE;
	}

	public void setCodigoOrgaoJulgador(Integer codigoOrgaoJulgador) {
		this.codigoOrgaoJulgador = codigoOrgaoJulgador;
	}

	public void setNomeOrgaoJulgador(String nomeOrgaoJulgador) {
		this.nomeOrgaoJulgador = nomeOrgaoJulgador;
	}

}