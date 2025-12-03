import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function formatDateTime(dateString) {
  if (!dateString) return "";
  
  const date = new Date(Number(dateString)*1000);

  return date.toLocaleDateString("pt-BR", {
	day: "2-digit",
	month: "2-digit",
	year: "numeric",
	hour: "2-digit",
	minute: "2-digit",
	second: "2-digit"
  });
}

export default function ProcessoList() {
  const [processos, setProcessos] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    carregar();
  }, []);

  const carregar = async () => {
    const resp = await api.get("/processos");

    const lista = Array.isArray(resp.data)
      ? resp.data
      : Array.isArray(resp.data.content)
      ? resp.data.content
      : [];

    setProcessos(lista);
  };

  const excluir = async (id) => {
    if (!window.confirm("Confirma exclusão?")) return;
    await api.delete(`/processos/${id}`);
    carregar();
  };

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Lista de Processos</h2>

        <button
          type="button"
          className="btn btn.success btn-success"
          onClick={() => navigate("/novo")}
        >
          Novo Processo
        </button>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Número</th>
            <th>Tribunal</th>
            <th>Grau</th>
            <th>Data ajuizamento</th>
            <th>Data última alteração</th>
            <th style={{ width: "180px" }}>Ações</th>
          </tr>
        </thead>
        <tbody>
          {processos.map((p) => (
            <tr key={p.id}>
              <td>{p.numeroProcesso}</td>
              <td>{p.tribunal}</td>
              <td>{p.grau}</td>
              <td>{formatDateTime(p.dataAjuizamento)}</td>
              <td>{formatDateTime(p.dataHoraUltimaAtualizacao)}</td>
              <td>
                <button
                  className="btn btn-warning btn-sm me-2"
                  onClick={() => navigate(`/editar/${p.id}`)}
                >
                  Editar
                </button>
                <button
                  className="btn btn-danger btn-sm btn-sm"
                  onClick={() => excluir(p.id)}
                >
                  Excluir
                </button>
              </td>
            </tr>
          ))}

          {processos.length === 0 && (
            <tr>
              <td colSpan="5" className="text-center">
                Nenhum processo encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
