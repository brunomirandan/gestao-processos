import React, { useEffect, useState } from "react";
import api from "../services/api";
import { hasRole } from "../services/auth";
import { useNavigate } from "react-router-dom";

export default function UserList() {
  const [usuarios, setUsuarios] = useState([]);
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    if (!hasRole("ROLE_ADMIN")) {
      navigate("/"); // se não for admin, manda pra home
      return;
    }
    carregar();
  }, []);

  async function carregar() {
    try {
      const resp = await api.get("/admin/users");
      setUsuarios(resp.data);
      setErro("");
    } catch (e) {
      setErro("Erro ao carregar usuários");
    }
  }

  async function alternarAtivo(user) {
    try {
      await api.put(`/admin/users/${user.id}`, {
        enabled: !user.enabled,
        roles: user.roles
      });
      setMensagem("Status atualizado");
      carregar();
    } catch (e) {
      setErro("Erro ao atualizar usuário");
    }
  }

  function novoUsuario() {
    navigate("/usuarios/novo");
  }

  function editarUsuario(id) {
    navigate(`/usuarios/${id}`);
  }

  return (
    <div className="container mt-4">
      <h2>Gerenciamento de Usuários</h2>

      {mensagem && (
        <div className="alert alert-success" role="alert">
          {mensagem}
        </div>
      )}
      {erro && (
        <div className="alert alert-danger" role="alert">
          {erro}
        </div>
      )}

      <div className="mb-3">
        <button className="btn btn-primary" onClick={novoUsuario}>
          Novo Usuário
        </button>
      </div>

      <table className="table table-striped">
        <thead>
          <tr>
            <th>Usuário</th>
            <th>Roles</th>
            <th>Ativo</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map((u) => (
            <tr key={u.id}>
              <td>{u.username}</td>
              <td>{u.roles.join(", ")}</td>
              <td>{u.enabled ? "Sim" : "Não"}</td>
              <td>
                <button
                  className="btn btn-sm btn-secondary me-2"
                  onClick={() => alternarAtivo(u)}
                >
                  {u.enabled ? "Desativar" : "Ativar"}
                </button>
                <button
                  className="btn btn-sm btn-primary"
                  onClick={() => editarUsuario(u.id)}
                >
                  Editar
                </button>
              </td>
            </tr>
          ))}

          {usuarios.length === 0 && (
            <tr>
              <td colSpan="4">Nenhum usuário cadastrado.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
