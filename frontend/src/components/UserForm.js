import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

export default function UserForm() {
  const { id } = useParams(); // se tiver id = edição, senão = novo
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [enabled, setEnabled] = useState(true);
  const [roleUser, setRoleUser] = useState(true);   // ROLE_USER por padrão
  const [roleAdmin, setRoleAdmin] = useState(false);
  const [newPassword, setNewPassword] = useState("");
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState("");

  const editando = !!id;

  useEffect(() => {
    if (editando) {
      carregarUsuario();
    }
  }, [id]);

  async function carregarUsuario() {
    try {
      const resp = await api.get(`/admin/users/${id}`);
      const u = resp.data;
      setUsername(u.username);
      setEnabled(u.enabled);
      setRoleUser(u.roles.includes("ROLE_USER"));
      setRoleAdmin(u.roles.includes("ROLE_ADMIN"));
    } catch (e) {
      setErro("Erro ao carregar usuário.");
    }
  }

  function montarRoles() {
    const roles = [];
    if (roleUser) roles.push("ROLE_USER");
    if (roleAdmin) roles.push("ROLE_ADMIN");
    return roles;
  }

  async function salvar(e) {
    e.preventDefault();
    setErro("");
    setMensagem("");

    const roles = montarRoles();
    if (roles.length === 0) {
      setErro("Selecione pelo menos uma permissão (role).");
      return;
    }

    try {
      if (!editando) {
        // CRIAÇÃO
        if (!newPassword) {
          setErro("Informe uma senha para o novo usuário.");
          return;
        }

        await api.post("/admin/users", {
          username,
          password: newPassword,
          roles
        });
        setMensagem("Usuário criado com sucesso.");
      } else {
        // ATUALIZAÇÃO DE ENABLED + ROLES
        await api.put(`/admin/users/${id}`, {
          enabled,
          roles
        });

        // SE INFORMOU UMA NOVA SENHA, FAZ RESET
        if (newPassword) {
          await api.put(`/admin/users/${id}/password`, {
            newPassword
          });
        }

        setMensagem("Usuário atualizado com sucesso.");
      }

      // Volta para lista após pequeno delay
      setTimeout(() => navigate("/usuarios"), 800);
    } catch (e) {
      setErro("Erro ao salvar usuário.");
    }
  }

  return (
    <div className="container mt-4" style={{ maxWidth: "600px" }}>
      <h2>{editando ? "Editar Usuário" : "Novo Usuário"}</h2>

      {mensagem && <div className="alert alert-success">{mensagem}</div>}
      {erro && <div className="alert alert-danger">{erro}</div>}

      <form onSubmit={salvar}>
        <div className="mb-3">
          <label className="form-label">Usuário (login)</label>
          <input
            className="form-control"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            disabled={editando} // não deixa trocar o login na edição
            required
          />
        </div>

        <div className="mb-3 form-check">
          <input
            type="checkbox"
            className="form-check-input"
            id="chkEnabled"
            checked={enabled}
            onChange={(e) => setEnabled(e.target.checked)}
          />
          <label className="form-check-label" htmlFor="chkEnabled">
            Usuário Ativo
          </label>
        </div>

        <div className="mb-3">
          <label className="form-label">Permissões (Roles)</label>
          <div className="form-check">
            <input
              type="checkbox"
              className="form-check-input"
              id="roleUser"
              checked={roleUser}
              onChange={(e) => setRoleUser(e.target.checked)}
            />
            <label className="form-check-label" htmlFor="roleUser">
              ROLE_USER
            </label>
          </div>

          <div className="form-check">
            <input
              type="checkbox"
              className="form-check-input"
              id="roleAdmin"
              checked={roleAdmin}
              onChange={(e) => setRoleAdmin(e.target.checked)}
            />
            <label className="form-check-label" htmlFor="roleAdmin">
              ROLE_ADMIN
            </label>
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label">
            {editando ? "Nova senha (opcional)" : "Senha"}
          </label>
          <input
            type="password"
            className="form-control"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder={editando ? "Deixe em branco para manter" : ""}
          />
        </div>

        <button type="submit" className="btn btn-primary me-2">
          Salvar
        </button>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => navigate("/usuarios")}
        >
          Voltar
        </button>
      </form>
    </div>
  );
}
