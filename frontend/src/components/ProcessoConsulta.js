import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

function formatarCpfCnpj(valor) {
  const digits = (valor || "").replace(/\D/g, "");
  if (!digits) return "";

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

function formatarDataHora(value) {
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

export default function ProcessoConsulta() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [erro, setErro] = useState("");
  const [processo, setProcesso] = useState({
    numeroProcesso: "",
    juizado: "",
    autores: [],
    reus: [],
    movimentos: [],
  });

  const [activeTab, setActiveTab] = useState("dados"); // 'dados' | 'movimentos'

  useEffect(() => {
    if (id) {
      carregarProcesso();
    }
  }, [id]);

  const carregarProcesso = async () => {
    try {
      const resp = await api.get(`/processos/${id}`);
      const data = resp.data || {};

      setProcesso({
        numeroProcesso: data.numeroProcesso || "",
        juizado: data.juizado || "",
        autores: data.autores || [],
        reus: data.reus || [],
        movimentos: data.movimentos || data.andamentos || [], // se o backend chamar "andamentos"
      });
    } catch (e) {
      console.error("Erro ao carregar processo", e);
      setErro("Erro ao carregar dados do processo.");
    }
  };

  const movimentosOrdenados = [...(processo.movimentos || [])].sort((a, b) => {
    const da = new Date(a.dataHora || a.data || a.dataMovimento);
    const db = new Date(b.dataHora || b.data || b.dataMovimento);
    return db - da; // ordem decrescente
  });

  return (
    <div className="container mt-4">
      <h2 className="mb-3">Consulta de Processo</h2>

      {erro && (
        <div className="alert alert-danger" role="alert">
          {erro}
        </div>
      )}

      <div className="mb-3 d-flex justify-content-between align-items-center">
        <div>
          <strong>Nº Processo: </strong> {processo.numeroProcesso || "-"}
          <br />
          <strong>Juizado: </strong> {processo.juizado || "-"}
        </div>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => navigate("/processos")}
        >
          Voltar
        </button>
      </div>

      {/* Abas */}
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button
            className={
              "nav-link " + (activeTab === "dados" ? "active" : "")
            }
            type="button"
            onClick={() => setActiveTab("dados")}
          >
            Dados do Processo
          </button>
        </li>
        <li className="nav-item">
          <button
            className={
              "nav-link " + (activeTab === "movimentos" ? "active" : "")
            }
            type="button"
            onClick={() => setActiveTab("movimentos")}
          >
            Movimentações
          </button>
        </li>
      </ul>

      {/* Conteúdo das abas */}
      {activeTab === "dados" && (
        <div>
          {/* Autores */}
          <div className="mb-4">
            <h5>Autores</h5>
            <ul className="list-group">
              {processo.autores && processo.autores.length > 0 ? (
                processo.autores.map((a) => (
                  <li key={a.id} className="list-group-item">
                    {a.nome} ({formatarCpfCnpj(a.identificacao)})
                  </li>
                ))
              ) : (
                <li className="list-group-item text-muted">
                  Nenhum autor cadastrado.
                </li>
              )}
            </ul>
          </div>

          {/* Réus */}
          <div className="mb-4">
            <h5>Réus</h5>
            <ul className="list-group">
              {processo.reus && processo.reus.length > 0 ? (
                processo.reus.map((r) => (
                  <li key={r.id} className="list-group-item">
                    {r.nome} ({formatarCpfCnpj(r.identificacao)})
                  </li>
                ))
              ) : (
                <li className="list-group-item text-muted">
                  Nenhum réu cadastrado.
                </li>
              )}
            </ul>
          </div>
        </div>
      )}

      {activeTab === "movimentos" && (
        <div>
          <h5>Movimentações (mais recentes primeiro)</h5>
          <ul className="list-group mt-2">
            {movimentosOrdenados && movimentosOrdenados.length > 0 ? (
              movimentosOrdenados.map((m, idx) => (
                <li
                  key={m.id || idx}
                  className="list-group-item d-flex justify-content-between align-items-start"
                >
                  <div>
                    <div>
                      <strong>{formatarDataHora(m.dataHora || m.data)}</strong>
                    </div>
                    <div>{m.codigo + " - " + m.nome}</div>
                  </div>
                </li>
              ))
            ) : (
              <li className="list-group-item text-muted">
                Nenhuma movimentação encontrada para este processo.
              </li>
            )}
          </ul>
        </div>
      )}
    </div>
  );
}
