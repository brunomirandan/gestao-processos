import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function formatDateTime(value) {
  if (!value) return "";

  // se vier em segundos (timestamp do backend), converte pra ms
  let date;
  const num = Number(value);
  if (!Number.isNaN(num)) {
    // se for um número "pequeno" tipo 10 dígitos, assumo que é segundos
    date = num.toString().length <= 10 ? new Date(num * 1000) : new Date(num);
  } else {
    // se vier como string ISO, tenta parse normal
    date = new Date(value);
  }

  if (Number.isNaN(date.getTime())) return "";

  const dd = String(date.getDate()).padStart(2, "0");
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const yyyy = date.getFullYear();

  const hh = String(date.getHours()).padStart(2, "0");
  const mi = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");

  return `${dd}/${mm}/${yyyy} ${hh}:${mi}:${ss}`;
}



export default function ProcessoList() {
  const [processos, setProcessos] = useState([]);
  const navigate = useNavigate();

  // --- verifica se usuário tem ROLE_ADMIN nas roles salvas no localStorage ---
  let isAdmin = false;
  try {
    const rolesJson = localStorage.getItem("roles");
    const roles = rolesJson ? JSON.parse(rolesJson) : [];

    // roles deve ser algo como ["ROLE_USER", "ROLE_ADMIN"]
    if (Array.isArray(roles)) {
      isAdmin = roles.includes("ROLE_ADMIN");
    }
  } catch (e) {
    console.warn("Erro ao ler roles do localStorage", e);
  }
  // ---------------------------------------------------------------------------

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
          className="btn btn-success"
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
            <th style={{ width: "220px" }}>Ações</th>
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
                <div className="d-flex gap-2">
                  <button
                    className="btn btn-info btn-sm"
                    onClick={() => navigate(`/processos/${p.id}/consulta`)}
                  >
                    Consultar
                  </button>

                  {isAdmin && (
                    <>
                      <button
                        className="btn btn-warning btn-sm"
                        onClick={() => navigate(`/editar/${p.id}`)}
                      >
                        Editar
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => excluir(p.id)}
                      >
                        Excluir
                      </button>
                    </>
                  )}
                </div>
              </td>
            </tr>
          ))}

          {processos.length === 0 && (
            <tr>
              <td colSpan="6" className="text-center">
                Nenhum processo encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
