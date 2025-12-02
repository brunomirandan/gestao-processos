import { Link, useNavigate } from "react-router-dom";
import { isAuthenticated, logout, getUsername, hasRole } from "../services/auth";

export default function Navbar() {
  const navigate = useNavigate();

  function sair() {
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar navbar-dark bg-dark px-3">
      {/* Logo / link para Home */}
      <Link to="/" className="navbar-brand">
        Gestão de Processos
      </Link>

      {isAuthenticated() && (
        <div className="d-flex align-items-center">
          <span className="text-white me-3">
            Usuário: {getUsername()}
          </span>

          <button className="btn btn-outline-light" onClick={sair}>
            Sair
          </button>
        </div>
      )}
    </nav>
  );
}
