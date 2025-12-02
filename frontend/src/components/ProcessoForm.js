import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";
import React, { useState, useEffect } from "react";


export default function ProcessoForm() {
	const { id } = useParams();
	const navigate = useNavigate();

	const [mensagem, setMensagem] = useState("");
	const [processo, setProcesso] = useState({
		id: "",
		numeroProcesso: "",
		tribunal: "",
		grau: ""
	});

	useEffect(() => { if (id) carregar(); }, [id]);

	const carregar = async () => {
		const resp = await api.get(`/processos/${id}`);
		setProcesso(resp.data);
	};

	const salvar = async (e) => {
		e.preventDefault();
		if (id) {
			await api.put(`/processos/${id}`, processo);
		} else {
			// se o ID é sua PK natural e você quer definir manualmente, preencha processo.id
			await api.post("/processos", processo);
		}
		navigate("/processos");
	};

	const importarAndamentos = async () => {
		if (!id) return;
		setMensagem("Importando andamentos...");
		try {
			const resp = await api.post(`/processos/${id}/importar-andamentos`);
			setMensagem(resp.data);
		} catch (err) {
			setMensagem("Erro ao importar: " + (err.response?.data || err.message));
		}
	};

	return (
		<div className="container">
			<h2 className="mb-3">{id ? "Editar Processo" : "Novo Processo"}</h2>
			
			{mensagem && (
							<div className="alert alert-info mt-3" role="alert">
								{mensagem}
							</div>
						)}
			
			<form onSubmit={salvar}>
				{!id && (
					<div className="mb-3">
						<label className="form-label">ID (chave)</label>
						<input
							type="text"
							className="form-control"
							value={processo.id}
							onChange={e => setProcesso({ ...processo, id: e.target.value })}
							placeholder="TRF1_436_JE_16403_00008323520184013202"
							required
						/>
					</div>
				)}

				<div className="mb-3">
					<label className="form-label">Número do Processo</label>
					<input
						type="text"
						className="form-control"
						value={processo.numeroProcesso}
						onChange={e => setProcesso({ ...processo, numeroProcesso: e.target.value })}
						required
					/>
				</div>

				<div className="mb-3">
					<label className="form-label">Tribunal</label>
					<input
						type="text"
						className="form-control"
						value={processo.tribunal || ""}
						onChange={e => setProcesso({ ...processo, tribunal: e.target.value })}
					/>
				</div>

				<div className="mb-3">
					<label className="form-label">Grau</label>
					<input
						type="text"
						className="form-control"
						value={processo.grau || ""}
						onChange={e => setProcesso({ ...processo, grau: e.target.value })}
					/>
				</div>

				<button type="submit" className="btn btn-success">Salvar</button>

				{id && (<button type="button" className="btn btn-warning" onClick={importarAndamentos} >Importar Andamentos</button>)}
			</form>

		</div>
	);
}

