import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

function formatarCpfCnpj(valor) {
  const digits = valor.replace(/\D/g, "");

  if (digits.length <= 11) {
    let v = digits;
    v = v.replace(/(\d{3})(\d)/, "$1.$2");
    v = v.replace(/(\d{3})(\d)/, "$1.$2");
    v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
    return v;
  } else {
    let v = digits;
    v = v.replace(/(\d{2})(\d)/, "$1.$2");
    v = v.replace(/(\d{3})(\d)/, "$1.$2");
    v = v.replace(/(\d{3})(\d)/, "$1/$2");
    v = v.replace(/(\d{4})(\d{1,2})$/, "$1-$2");
    return v;
  }
}

export default function ParteForm() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [nome, setNome] = useState("");
  const [identificacao, setIdentificacao] = useState("");
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");

  const editando = !!id;

  useEffect(() => {
    if (editando) {
      api
        .get(`/partes/${id}`)
        .then((resp) => {
          setNome(resp.data.nome);
          setIdentificacao(formatarCpfCnpj(resp.data.identificacao || ""));
        })
        .catch(() => {
          setErro("Erro ao carregar dados da parte.");
        });
    }
  }, [editando, id]);

  const handleIdentificacaoChange = (e) => {
    const valor = e.target.value;
    setIdentificacao(formatarCpfCnpj(valor));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro("");
    setSucesso("");

    const somenteDigitos = identificacao.replace(/\D/g, "");
    const dados = { nome, identificacao: somenteDigitos };

    try {
      if (editando) {
        await api.put(`/partes/${id}`, dados);
        setSucesso("Parte atualizada com sucesso!");
      } else {
        await api.post("/partes", dados);
        setSucesso("Parte cadastrada com sucesso!");
      }

      setTimeout(() => navigate("/partes"), 1200);

    } catch (e) {
      const msg =
        e.response?.data?.message ||
        "Erro ao salvar parte. Tente novamente.";
      setErro(msg);
    }
  };

  return (
    <div className="container mt-4">
      <h2>{editando ? "Editar Parte" : "Nova Parte"}</h2>

      {sucesso && (
        <div className="alert alert-success mt-3" role="alert">
          {sucesso}
        </div>
      )}

      {erro && (
        <div className="alert alert-danger mt-3" role="alert">
          {erro}
        </div>
      )}

      <form className="mt-3" onSubmit={handleSubmit}>

        <div className="mb-3">
          <label className="form-label">Nome</label>
          <input
            type="text"
            className="form-control"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Identificação (CPF/CNPJ)</label>
          <input
            type="text"
            className="form-control"
            value={identificacao}
            onChange={handleIdentificacaoChange}
            required
          />
        </div>

        <button type="submit" className="btn btn-primary me-2">
          Salvar
        </button>

        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => navigate("/partes")}
        >
          Cancelar
        </button>

      </form>
    </div>
  );
}
