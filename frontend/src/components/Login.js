import { useState } from "react";
import api from "../services/api";
import { loginSuccess } from "../services/auth";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  async function logar(e) {
    e.preventDefault();

    try {
      const resp = await api.post("/auth/login", {
        username,
        password
      });

      loginSuccess(resp.data.token, resp.data.username, resp.data.roles);

      navigate("/"); // volta para lista
    } catch (err) {
      setErro("Usuário ou senha inválidos");
    }
  }

  return (
    <div className="container mt-5" style={{ maxWidth: "400px" }}>
      <h3>Login</h3>

      {erro && <div className="alert alert-danger">{erro}</div>}

      <form onSubmit={logar}>
        <div className="mb-3">
          <label>Usuário</label>
          <input
            className="form-control"
            value={username}
            onChange={e => setUsername(e.target.value)}
          />
        </div>

        <div className="mb-3">
          <label>Senha</label>
          <input
            type="password"
            className="form-control"
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </div>

        <button className="btn btn-dark w-100">Entrar</button>
      </form>
    </div>
  );
}
