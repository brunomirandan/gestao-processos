import React, { useState } from "react";
import api from "../services/api";

export default function ImportarProcessos() {
  const [arquivo, setArquivo] = useState(null);
  const [mensagem, setMensagem] = useState("");
  const [progresso, setProgresso] = useState(0);
  const [carregando, setCarregando] = useState(false);

  const handleFileChange = (e) => {
    setArquivo(e.target.files[0]);
    setMensagem("");
    setProgresso(0);
  };

  const enviarArquivo = async (e) => {
    e.preventDefault();
    setMensagem("");
    setProgresso(0);

    if (!arquivo) {
      setMensagem("Selecione um arquivo Excel (.xlsx)");
      return;
    }

    const formData = new FormData();
    formData.append("file", arquivo);

    try {
      setCarregando(true);

      const resp = await api.post("/processos/importar", formData, {
        headers: { "Content-Type": "multipart/form-data" },

        // progresso real do upload (até ~80%)
        onUploadProgress: (event) => {
          if (!event.total) return;
          const percent = Math.round((event.loaded * 80) / event.total);
          setProgresso(percent);
        },
      });

      // upload acabou + backend respondeu -> 100%
      setProgresso(100);
      setMensagem(
        typeof resp.data === "string"
          ? resp.data
          : JSON.stringify(resp.data, null, 2)
      );
      setArquivo(null);
    } catch (err) {
      setMensagem(
        "Erro ao importar: " + (err.response?.data || err.message)
      );
      setProgresso(0);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="container mt-4">
      <h2>Importar Processos via Excel</h2>
      <form onSubmit={enviarArquivo}>
        <div className="mb-3">
          <input
            type="file"
            accept=".xlsx"
            className="form-control"
            onChange={handleFileChange}
          />
        </div>

        {/* Barra de progresso */}
        {(carregando || progresso > 0) && (
          <div className="mb-3">
            <label className="form-label">Progresso da importação</label>
            <div className="progress">
              <div
                className={
                  "progress-bar progress-bar-striped " +
                  (carregando ? "progress-bar-animated" : "")
                }
                role="progressbar"
                style={{ width: `${progresso}%` }}
                aria-valuenow={progresso}
                aria-valuemin="0"
                aria-valuemax="100"
              >
                {progresso}%
              </div>
            </div>
          </div>
        )}

        <button
          type="submit"
          className="btn btn-primary"
          disabled={carregando}
        >
          {carregando ? "Importando..." : "Enviar"}
        </button>
      </form>

      {mensagem && (
        <div className="alert alert-info mt-3" role="alert">
          <pre className="mb-0" style={{ whiteSpace: "pre-wrap" }}>
            {mensagem}
          </pre>
        </div>
      )}
    </div>
  );
}