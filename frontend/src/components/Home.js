import React from "react";
import { Link } from "react-router-dom";
import { getUsername, hasRole } from "../services/auth";

export default function Home() {
	const username = getUsername();

	return (
		<div className="container mt-4">
			<h2 className="mb-3">Bem-vindo{username ? `, ${username}` : ""}!</h2>
			<p>Escolha uma das opções abaixo, organizadas por módulo:</p>

			{hasRole("ROLE_ADMIN") && (
				<section className="mt-5">
					<h4>Módulo de Configuração</h4>
					<p className="text-muted">
						Administração de usuários e permissões do sistema.
					</p>

					<div className="row mt-3">
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
					</div>
				</section>
			)}

			<section className="mt-4">
				<h4>Módulo Gerencial</h4>
				<p className="text-muted">
					Cadastros e consultas operacionais de processos e partes.
				</p>

				<div className="row mt-3">
					{/* Partes */}
					<div className="col-md-4 mb-3">
						<div className="card h-100">
							<div className="card-body d-flex flex-column">
								<h5 className="card-title">Partes</h5>
								<p className="card-text">
									Cadastre partes envolvidas e consulte suas informações.
								</p>
								<Link to="/partes" className="btn btn-success mt-auto">
									Gerenciar Partes
								</Link>
							</div>
						</div>
					</div>

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
				</div>
			</section>

			{hasRole("ROLE_ADMIN") && (
				<section className="mt-5">
					<h4>Módulo de Importação</h4>
					<p className="text-muted">
						Importação em lote de processos a partir de arquivos externos.
					</p>

					<div className="row mt-3">
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
					</div>
				</section>
			)}

			{/* Dashboard - pode ser só para ADMIN se quiser */}
			{hasRole("ROLE_ADMIN") && (
				<section className="mt-5">
					<h4>Módulo de Relatórios</h4>
					<div className="col-md-4 mb-3">
						<div className="card h-100">
							<div className="card-body d-flex flex-column">
								<h5 className="card-title">Dashboard</h5>
								<p className="card-text">
									Visualize estatísticas dos processos por tribunal.
								</p>
								<Link to="/dashboard" className="btn btn-info mt-auto">
									Ver Dashboard
								</Link>
							</div>
						</div>
					</div>
				</section>
			)}
		</div>
	);
}
