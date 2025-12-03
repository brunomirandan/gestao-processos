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

export default function ProcessoForm() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [mensagemImportacao, setMensagemImportacao] = useState("");

  const [partes, setPartes] = useState([]);
  const [autorId, setAutorId] = useState("");
  const [reuId, setReuId] = useState("");

  const [juizados, setJuizados] = useState([]);

  const [processo, setProcesso] = useState({
    numeroProcesso: "",
    juizado: "",
    autores: [],
    reus: [],
  });

  // carrega partes e juizados na inicialização
  useEffect(() => {
    carregarPartes();
    carregarJuizados();
  }, []);

  // se estiver editando, carrega o processo
  useEffect(() => {
    if (id) {
      carregarProcesso();
    }
  }, [id]);

  const carregarPartes = async () => {
    try {
      const resp = await api.get("/partes");
      const lista = Array.isArray(resp.data)
        ? resp.data
        : Array.isArray(resp.data.content)
        ? resp.data.content
        : [];
      setPartes(lista);
    } catch (e) {
      console.error("Erro ao carregar partes", e);
    }
  };

  // Busca os valores do enum DatajudEndpoint no backend
  const carregarJuizados = async () => {
    try {
      const resp = await api.get("/juizados");
      // espera algo como [{nome: "JUIZADO_TRF1", descricao: "..."}]
      setJuizados(resp.data || []);
    } catch (e) {
      console.error("Erro ao carregar juizados", e);
    }
  };

  const carregarProcesso = async () => {
    try {
      const resp = await api.get(`/processos/${id}`);
      const data = resp.data || {};
      setProcesso({
        numeroProcesso: data.numeroProcesso || "",
        juizado: data.juizado || "",
        autores: data.autores || [],
        reus: data.reus || [],
      });
    } catch (e) {
      console.error("Erro ao carregar processo", e);
      setErro("Erro ao carregar dados do processo.");
    }
  };

  const adicionarAutor = () => {
    setErro("");
    if (!autorId) return;

    const parte = partes.find((p) => String(p.id) === String(autorId));
    if (!parte) return;

    const autoresIds = (processo.autores || []).map((a) => a.id);
    const reusIds = (processo.reus || []).map((r) => r.id);

    if (reusIds.includes(parte.id)) {
      setErro("Uma parte não pode ser Autor e Réu no mesmo processo.");
      return;
    }

    if (autoresIds.includes(parte.id)) {
      setErro("A lista de Autores já contém essa parte.");
      return;
    }

    setProcesso((prev) => ({
      ...prev,
      autores: [...prev.autores, parte],
    }));
  };

  const removerAutor = (idParte) => {
    setProcesso((prev) => ({
      ...prev,
      autores: prev.autores.filter((a) => a.id !== idParte),
    }));
  };

  const adicionarReu = () => {
    setErro("");
    if (!reuId) return;

    const parte = partes.find((p) => String(p.id) === String(reuId));
    if (!parte) return;

    const autoresIds = (processo.autores || []).map((a) => a.id);
    const reusIds = (processo.reus || []).map((r) => r.id);

    if (autoresIds.includes(parte.id)) {
      setErro("Uma parte não pode ser Autor e Réu no mesmo processo.");
      return;
    }

    if (reusIds.includes(parte.id)) {
      setErro("A lista de Réus já contém essa parte.");
      return;
    }

    setProcesso((prev) => ({
      ...prev,
      reus: [...prev.reus, parte],
    }));
  };

  const removerReu = (idParte) => {
    setProcesso((prev) => ({
      ...prev,
      reus: prev.reus.filter((r) => r.id !== idParte),
    }));
  };

  const validarAntesDeSalvar = () => {
    if (!processo.numeroProcesso.trim()) {
      setErro("O número do processo deve ser informado.");
      return false;
    }

    if (!processo.juizado) {
      setErro("Selecione o juizado ao qual o processo pertence.");
      return false;
    }

    const autoresIds = (processo.autores || []).map((a) => a.id);
    const reusIds = (processo.reus || []).map((r) => r.id);

    if (new Set(autoresIds).size !== autoresIds.length) {
      setErro("A lista de autores contém registros repetidos.");
      return false;
    }

    if (new Set(reusIds).size !== reusIds.length) {
      setErro("A lista de réus contém registros repetidos.");
      return false;
    }

    const inter = autoresIds.filter((id) => reusIds.includes(id));
    if (inter.length > 0) {
      setErro("Uma mesma parte não pode ser Autor e Réu no mesmo processo.");
      return false;
    }

    return true;
  };

  const salvar = async (e) => {
    e.preventDefault();
    setErro("");
    setSucesso("");
    setMensagemImportacao("");

    if (!validarAntesDeSalvar()) {
      return;
    }

    try {
      const payload = {
        numeroProcesso: processo.numeroProcesso,
        juizado: processo.juizado,
        autores: (processo.autores || []).map((a) => ({ id: a.id })),
        reus: (processo.reus || []).map((r) => ({ id: r.id })),
      };

      if (id) {
        await api.put(`/processos/${id}`, payload);
        setSucesso(
          "Processo atualizado com sucesso! Importação de andamentos realizada automaticamente."
        );
      } else {
        await api.post("/processos", payload);
        setSucesso(
          "Processo cadastrado com sucesso! Importação de andamentos realizada automaticamente."
        );
      }

      setTimeout(() => navigate("/processos"), 1500);
    } catch (err) {
      console.error("Erro ao salvar processo", err);
      const msg =
        err.response?.data?.message ||
        "Erro ao salvar processo. Verifique os dados e tente novamente.";
      setErro(msg);
    }
  };

  const importarAndamentos = async () => {
    if (!id) return;
    setMensagemImportacao("Importando andamentos...");
    try {
      const resp = await api.post(`/processos/${id}/importar-andamentos`);
      setMensagemImportacao(resp.data || "Importação concluída.");
    } catch (err) {
      setMensagemImportacao(
        "Erro ao importar: " + (err.response?.data || err.message)
      );
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-3">{id ? "Editar Processo" : "Novo Processo"}</h2>

      {sucesso && (
        <div className="alert alert-success" role="alert">
          {sucesso}
        </div>
      )}

      {erro && (
        <div className="alert alert-danger" role="alert">
          {erro}
        </div>
      )}

      {mensagemImportacao && (
        <div className="alert alert-info mt-3" role="alert">
          {mensagemImportacao}
        </div>
      )}

      <form onSubmit={salvar} className="mt-3">
        {/* Número do processo */}
        <div className="mb-3">
          <label className="form-label">Número do Processo</label>
          <input
            type="text"
            className="form-control"
            value={processo.numeroProcesso}
            onChange={(e) =>
              setProcesso({ ...processo, numeroProcesso: e.target.value })
            }
            required
          />
        </div>

        {/* Juizado (enum DatajudEndpoint) */}
        <div className="mb-3">
          <label className="form-label">Juizado</label>
          <select
            className="form-select"
            value={processo.juizado}
            onChange={(e) =>
              setProcesso({ ...processo, juizado: e.target.value })
            }
            required
          >
            <option value="">Selecione...</option>
            {juizados.map((j) => (
              <option key={j.nome || j.name} value={j.nome || j.name}>
                {j.descricao || j.label || j.nome || j.name}
              </option>
            ))}
          </select>
        </div>

        {/* Autores */}
        <div className="mb-3">
          <label className="form-label">Autores (Partes)</label>
          <div className="d-flex gap-2 mb-2">
            <select
              className="form-select"
              value={autorId}
              onChange={(e) => setAutorId(e.target.value)}
            >
              <option value="">Selecione uma parte...</option>
              {partes.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nome} ({formatarCpfCnpj(p.identificacao)})
                </option>
              ))}
            </select>
            <button
              type="button"
              className="btn btn-outline-primary"
              onClick={adicionarAutor}
            >
              Adicionar Autor
            </button>
          </div>

          <ul className="list-group">
            {processo.autores && processo.autores.length > 0 ? (
              processo.autores.map((a) => (
                <li
                  key={a.id}
                  className="list-group-item d-flex justify-content-between align-items-center"
                >
                  <span>
                    {a.nome} ({formatarCpfCnpj(a.identificacao)})
                  </span>
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-danger"
                    onClick={() => removerAutor(a.id)}
                  >
                    Remover
                  </button>
                </li>
              ))
            ) : (
              <li className="list-group-item text-muted">
                Nenhum autor selecionado.
              </li>
            )}
          </ul>
        </div>

        {/* Réus */}
        <div className="mb-3">
          <label className="form-label">Réus (Partes)</label>
          <div className="d-flex gap-2 mb-2">
            <select
              className="form-select"
              value={reuId}
              onChange={(e) => setReuId(e.target.value)}
            >
              <option value="">Selecione uma parte...</option>
              {partes.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nome} ({formatarCpfCnpj(p.identificacao)})
                </option>
              ))}
            </select>
            <button
              type="button"
              className="btn btn-outline-primary"
              onClick={adicionarReu}
            >
              Adicionar Réu
            </button>
          </div>

          <ul className="list-group">
            {processo.reus && processo.reus.length > 0 ? (
              processo.reus.map((r) => (
                <li
                  key={r.id}
                  className="list-group-item d-flex justify-content-between align-items-center"
                >
                  <span>
                    {r.nome} ({formatarCpfCnpj(r.identificacao)})
                  </span>
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-danger"
                    onClick={() => removerReu(r.id)}
                  >
                    Remover
                  </button>
                </li>
              ))
            ) : (
              <li className="list-group-item text-muted">
                Nenhum réu selecionado.
              </li>
            )}
          </ul>
        </div>

        <button type="submit" className="btn btn-success me-2">
          Salvar
        </button>

        {id && (
          <button
            type="button"
            className="btn btn-warning"
            onClick={importarAndamentos}
          >
            Importar Andamentos (manual)
          </button>
        )}
      </form>
    </div>
  );
}
