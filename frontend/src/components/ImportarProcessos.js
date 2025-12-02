import React, { useState } from "react";
import api from "../services/api";

export default function ImportarProcessos() {
  const [arquivo, setArquivo] = useState(null);
  const [mensagem, setMensagem] = useState("");

  const handleFileChange = (e) => {
    setArquivo(e.target.files[0]);
  };

  const enviarArquivo = async (e) => {
    e.preventDefault();
    if (!arquivo) {
      setMensagem("Selecione um arquivo Excel (.xlsx)");
      return;
    }

    const formData = new FormData();
    formData.append("file", arquivo);

    try {
      const resp = await api.post("/processos/importar", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setMensagem(resp.data);
      setArquivo(null);
    } catch (err) {
      setMensagem("Erro ao importar: " + (err.response?.data || err.message));
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
        <button type="submit" className="btn btn-primary">
          Enviar
        </button>
      </form>

      {mensagem && (
        <div className="alert alert-info mt-3" role="alert">
          {mensagem}
        </div>
      )}
    </div>
  );
}
