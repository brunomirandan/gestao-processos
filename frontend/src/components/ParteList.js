import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
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

export default function ParteList() {
  const [partes, setPartes] = useState([]);
  const [nome, setNome] = useState("");
  const [identificacao, setIdentificacao] = useState("");
  const [sucesso, setSucesso] = useState("");

  const [page, setPage] = useState(0);        // página atual (0-based)
  const [size] = useState(10);                // itens por página
  const [totalPages, setTotalPages] = useState(0);

  const [sortField, setSortField] = useState("nome");
  const [sortDirection, setSortDirection] = useState("asc");

  const navigate = useNavigate();

  const carregar = async () => {
    try {
      const params = {
        page,
        size,
        sort: `${sortField},${sortDirection}`,
      };
      if (nome) params.nome = nome;
      if (identificacao) params.identificacao = identificacao;

      const response = await api.get("/partes", { params });

      setPartes(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
    } catch {
      setSucesso("");
      alert("Erro ao carregar partes.");
    }
  };

  useEffect(() => {
    carregar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, sortField, sortDirection]);

  const excluir = async (id) => {
    if (!window.confirm("Confirma a exclusão desta parte?")) return;

    try {
      await api.delete(`/partes/${id}`);
      setSucesso("Parte excluída com sucesso!");
      // Se excluir o último item da página, pode ajustar page depois se quiser
      carregar();
    } catch {
      alert("Erro ao excluir parte.");
    }
  };

  const handlePesquisar = () => {
    setPage(0); // sempre volta pra primeira página ao filtrar
    carregar();
  };

  const handleSort = (campo) => {
    if (sortField === campo) {
      setSortDirection((prev) => (prev === "asc" ? "desc" : "asc"));
    } else {
      setSortField(campo);
      setSortDirection("asc");
    }
  };

  const proximaPagina = () => {
    if (page + 1 < totalPages) {
      setPage(page + 1);
    }
  };

  const paginaAnterior = () => {
    if (page > 0) {
      setPage(page - 1);
    }
  };

  const indicadorOrdenacao = (campo) => {
    if (campo !== sortField) return "";
    return sortDirection === "asc" ? " ▲" : " ▼";
  };

  return (
    <div className="container mt-4">
      <h2>Partes</h2>

      {sucesso && (
        <div className="alert alert-success mt-3" role="alert">
          {sucesso}
        </div>
      )}

      <div className="row mb-3 mt-3">
        <div className="col-md-4">
          <input
            type="text"
            className="form-control"
            placeholder="Filtrar por nome"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
          />
        </div>

        <div className="col-md-4">
          <input
            type="text"
            className="form-control"
            placeholder="Filtrar por identificação"
            value={identificacao}
            onChange={(e) => setIdentificacao(e.target.value)}
          />
        </div>

        <div className="col-md-4 d-flex gap-2">
          <button className="btn btn-primary" onClick={handlePesquisar}>
            Pesquisar
          </button>
          <button
            className="btn btn-success"
            onClick={() => navigate("/partes/novo")}
          >
            Nova Parte
          </button>
        </div>
      </div>

      <table className="table table-striped">
        <thead>
          <tr>
            <th
              style={{ cursor: "pointer" }}
              onClick={() => handleSort("nome")}
            >
              Nome{indicadorOrdenacao("nome")}
            </th>
            <th
              style={{ cursor: "pointer" }}
              onClick={() => handleSort("identificacao")}
            >
              Identificação{indicadorOrdenacao("identificacao")}
            </th>
            <th style={{ width: "150px" }}>Ações</th>
          </tr>
        </thead>

        <tbody>
          {partes.length === 0 && (
            <tr>
              <td colSpan="3">Nenhuma parte encontrada.</td>
            </tr>
          )}

          {partes.map((p) => (
            <tr key={p.id}>
              <td>{p.nome}</td>
              <td>{formatarCpfCnpj(p.identificacao)}</td>
              <td>
                <button
                  className="btn btn-sm btn-warning me-2"
                  onClick={() => navigate(`/partes/${p.id}`)}
                >
                  Editar
                </button>

                <button
                  className="btn btn-sm btn-danger"
                  onClick={() => excluir(p.id)}
                >
                  Excluir
                </button>
              </td>
            </tr>
          ))}
        </tbody>

      </table>

      {/* Paginação */}
      {totalPages > 1 && (
        <div className="d-flex justify-content-between align-items-center">
          <button
            className="btn btn-outline-secondary"
            onClick={paginaAnterior}
            disabled={page === 0}
          >
            Anterior
          </button>

          <span>
            Página {page + 1} de {totalPages}
          </span>

          <button
            className="btn btn-outline-secondary"
            onClick={proximaPagina}
            disabled={page + 1 >= totalPages}
          >
            Próxima
          </button>
        </div>
      )}
    </div>
  );
}
