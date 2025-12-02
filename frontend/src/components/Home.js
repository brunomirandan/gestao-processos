import React from "react";
import { Link } from "react-router-dom";
import { getUsername, hasRole } from "../services/auth";

export default function Home() {
  const username = getUsername();

  return (
    <div className="container mt-4">
      <h2 className="mb-3">Bem-vindo{username ? `, ${username}` : ""}!</h2>
      <p>Escolha uma das opções abaixo:</p>

      <div className="row mt-4">
        {/* Processos */}
        <div className="col-md-4 mb-3">
          <div className="card h-100">
            <div className="card-body d-flex flex-column">
              <h5 className="card-title">Processos</h5>
              <p className="card-text">
                Consulte, edite e acompanhe os processos cadastrados.
              </p>
              <Link to="/processos" className="btn btn-primary mt-auto">
                Ir para Lista de Processos
              </Link>
            </div>
          </div>
        </div>

        {/* Importar Excel (apenas ADMIN) */}
        {hasRole("ROLE_ADMIN") && (
          <div className="col-md-4 mb-3">
            <div className="card h-100">
              <div className="card-body d-flex flex-column">
                <h5 className="card-title">Importar Processos (Excel)</h5>
                <p className="card-text">
                  Importe novos processos a partir de arquivos Excel.
                </p>
                <Link to="/importar" className="btn btn-secondary mt-auto">
                  Importar Excel
                </Link>
              </div>
            </div>
          </div>
        )}

        {/* Usuários (apenas ADMIN) */}
        {hasRole("ROLE_ADMIN") && (
          <div className="col-md-4 mb-3">
            <div className="card h-100">
              <div className="card-body d-flex flex-column">
                <h5 className="card-title">Gerenciamento de Usuários</h5>
                <p className="card-text">
                  Cadastre novos usuários, altere permissões e redefina senhas.
                </p>
                <Link to="/usuarios" className="btn btn-warning mt-auto">
                  Gerenciar Usuários
                </Link>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
