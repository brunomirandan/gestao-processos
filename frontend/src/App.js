import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import ProcessoList from "./components/ProcessoList";
import ProcessoForm from "./components/ProcessoForm";
import ImportarProcessos from "./components/ImportarProcessos";
import Login from "./components/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import UserList from "./components/UserList";
import UserForm from "./components/UserForm";
import Home from "./components/Home";
import ParteList from "./components/ParteList";
import ParteForm from "./components/ParteForm";
import Dashboard from "./components/Dashboard";
import ProcessoConsulta from "./components/ProcessoConsulta";




export default function App() {
	return (
		<>
			<Navbar />
			<Routes>
				<Route path="/login" element={<Login />} />

				{/* Tela inicial após login */}
				<Route
					path="/"
					element={
						<ProtectedRoute>
							<Home />
						</ProtectedRoute>
					}
				/>

				{/* Processos */}
				<Route
					path="/processos"
					element={
						<ProtectedRoute>
							<ProcessoList />
						</ProtectedRoute>
					}
				/>
				<Route
					path="/novo"
					element={
						<ProtectedRoute>
							<ProcessoForm />
						</ProtectedRoute>
					}
				/>
				<Route
					path="/editar/:id"
					element={
						<ProtectedRoute>
							<ProcessoForm />
						</ProtectedRoute>
					}
				/>

				{/* Importar Excel */}
				<Route
					path="/importar"
					element={
						<ProtectedRoute>
							<ImportarProcessos />
						</ProtectedRoute>
					}
				/>

				{/* Usuários (somente ADMIN, mas ProtectedRoute já exige login;
            controle fino está no backend com @PreAuthorize) */}
				<Route
					path="/usuarios"
					element={
						<ProtectedRoute>
							<UserList />
						</ProtectedRoute>
					}
				/>
				<Route
					path="/usuarios/novo"
					element={
						<ProtectedRoute>
							<UserForm />
						</ProtectedRoute>
					}
				/>
				<Route
					path="/usuarios/:id"
					element={
						<ProtectedRoute>
							<UserForm />
						</ProtectedRoute>
					}
				/>

				<Route
					path="/partes"
					element={
						<ProtectedRoute>
							<ParteList />
						</ProtectedRoute>
					}
				/>

				<Route
					path="/partes/novo"
					element={
						<ProtectedRoute>
							<ParteForm />
						</ProtectedRoute>
					}
				/>

				<Route
					path="/partes/:id"
					element={
						<ProtectedRoute>
							<ParteForm />
						</ProtectedRoute>
					}
				/>

				<Route
					path="/dashboard"
					element={
						<ProtectedRoute>
							<Dashboard />
						</ProtectedRoute>
					}
				/>

				<Route
					path="/processos/:id/consulta"
					element={
						<ProtectedRoute>
							<ProcessoConsulta />
						</ProtectedRoute>
					}
				/>
			</Routes>
		</>
	);
}
