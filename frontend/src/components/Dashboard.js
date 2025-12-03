import React, { useEffect, useState } from "react";
import api from "../services/api";

import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Tooltip,
  Legend,
} from "chart.js";
import { Bar } from "react-chartjs-2";

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend);

export default function Dashboard() {
  const [dados, setDados] = useState([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    carregar();
  }, []);

  async function carregar() {
    try {
      const resp = await api.get(
        "/processos/relatorios/quantidade-por-tribunal"
      );
      setDados(resp.data || []);
    } catch (e) {
      console.error(e);
      setErro("Erro ao carregar dados do dashboard.");
    }
  }

  const labels = dados.map((d) => d.tribunal || "N/D");
  const valores = dados.map((d) => d.quantidade || 0);

  const data = {
    labels,
    datasets: [
      {
        label: "Quantidade de Processos",
        data: valores,
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: "top",
      },
      tooltip: {
        enabled: true,
      },
    },
    scales: {
      x: {
        title: {
          display: true,
          text: "Tribunal",
        },
      },
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: "Quantidade de Processos",
        },
        ticks: {
          precision: 0,
        },
      },
    },
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-3">Dashboard de Processos</h2>
      <p className="text-muted">
        Distribuição de processos cadastrados por tribunal.
      </p>

      {erro && <div className="alert alert-danger">{erro}</div>}

      <div className="row mt-4">
        {/* Gráfico */}
        <div className="col-md-7 mb-4">
          <div className="card h-100">
            <div className="card-body">
              <h5 className="card-title">Processos por Tribunal (Gráfico)</h5>
              {dados.length === 0 ? (
                <p className="text-muted mt-3">
                  Não há dados suficientes para exibir o gráfico.
                </p>
              ) : (
                <Bar data={data} options={options} />
              )}
            </div>
          </div>
        </div>

        {/* Tabela */}
        <div className="col-md-5 mb-4">
          <div className="card h-100">
            <div className="card-body d-flex flex-column">
              <h5 className="card-title">Processos por Tribunal (Tabela)</h5>

              <table className="table table-sm mt-3">
                <thead>
                  <tr>
                    <th>Tribunal</th>
                    <th className="text-end">Quantidade</th>
                  </tr>
                </thead>
                <tbody>
                  {dados.map((d, idx) => (
                    <tr key={idx}>
                      <td>{d.tribunal || "Não informado"}</td>
                      <td className="text-end">{d.quantidade}</td>
                    </tr>
                  ))}
                  {dados.length === 0 && (
                    <tr>
                      <td colSpan="2" className="text-center text-muted">
                        Nenhum dado encontrado.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>

              <div className="mt-auto">
                <p className="mt-3 mb-0">
                  <strong>Total de tribunais:</strong> {dados.length}
                </p>
                <p className="mb-0">
                  <strong>Total de processos:</strong>{" "}
                  {dados.reduce((acc, d) => acc + (d.quantidade || 0), 0)}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
