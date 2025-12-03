package com.juridico.processos.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juridico.processos.enums.DatajudEndpoint;
import com.juridico.processos.model.Assunto;
import com.juridico.processos.model.Classe;
import com.juridico.processos.model.Formato;
import com.juridico.processos.model.Movimento;
import com.juridico.processos.model.OrgaoJulgador;
import com.juridico.processos.model.Processo;
import com.juridico.processos.model.Sistema;
import com.juridico.processos.repository.MovimentoRepository;
import com.juridico.processos.repository.ProcessoRepository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class DatajudImportService {

	private final ProcessoRepository processoRepository;
	private final MovimentoRepository movimentoRepository;

	@Value("${datajud.api.key}")
	private String apiKey;

	public DatajudImportService(ProcessoRepository processoRepository, MovimentoRepository movimentoRepository) {
		this.processoRepository = processoRepository;
		this.movimentoRepository = movimentoRepository;
	}

	@Transactional
	public String importarProcessoCompleto(DatajudEndpoint endpoint, String nProcesso) {
		Processo processoExistente = processoRepository.findByNumeroProcesso(nProcesso)
				.orElseThrow(() -> new RuntimeException("Processo não encontrado: " + nProcesso));

		String numeroProcesso = processoExistente.getNumeroProcesso();

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");

		String jsonQuery = "{\n" + "  \"query\": {\n" + "    \"match\": {\n" + "      \"numeroProcesso\": \""
				+ numeroProcesso + "\"\n" + "    }\n" + "  }\n" + "}";

		RequestBody body = RequestBody.create(mediaType, jsonQuery);
		Request request = new Request.Builder().url(endpoint.getEndereco()).method("POST", body)
				.addHeader("Authorization", "APIKey " + apiKey).addHeader("Content-Type", "application/json").build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful())
				throw new IOException("Erro HTTP: " + response.code());

			String json = response.body().string();
			JSONObject root = new JSONObject(json);
			JSONArray hits = root.getJSONObject("hits").getJSONArray("hits");

			if (hits.isEmpty()) {
				return "Nenhum processo encontrado na API DataJud para o número " + numeroProcesso;
			}

			// Pega o primeiro resultado
			JSONObject source = hits.getJSONObject(0).getJSONObject("_source");

			// Atualiza dados principais do processo
			processoExistente.setTribunal(source.optString("tribunal"));
			processoExistente.setGrau(source.optString("grau"));

			Instant dataAjuizamento = converteData(source.optString("dataAjuizamento"));
			processoExistente.setDataAjuizamento(dataAjuizamento);

			Instant dataHoraUltimaAtualizacao = converteData(source.optString("dataHoraUltimaAtualizacao"));
			processoExistente.setDataHoraUltimaAtualizacao(dataHoraUltimaAtualizacao);

			processoExistente.setNivelSigilo(source.optInt("nivelSigilo", 0));

			// Classe
			if (source.has("classe")) {
				JSONObject classe = source.getJSONObject("classe");
				Classe classeInfo = new Classe();
				classeInfo.setCodigoClasse(classe.optInt("codigo"));
				classeInfo.setNomeClasse(classe.optString("nome"));
				processoExistente.setClasse(classeInfo);
			}

			// Sistema
			if (source.has("sistema")) {
				JSONObject sistemaJSON = source.getJSONObject("sistema");
				Sistema sistema = new Sistema();
				sistema.setCodigoSistema(sistemaJSON.optInt("codigo"));
				sistema.setNomeSistema(sistemaJSON.optString("nome"));
				processoExistente.setSistema(sistema);
			}

			// Formato
			if (source.has("formato")) {
				JSONObject formato = source.getJSONObject("formato");
				Formato formatoInfo = new Formato();
				formatoInfo.setCodigoFormato(formato.optInt("codigo"));
				formatoInfo.setNomeFormato(formato.optString("nome"));
				processoExistente.setFormato(formatoInfo);
			}

			// Ã“rgÃ£o julgador
			if (source.has("orgaoJulgador")) {
				JSONObject orgao = source.getJSONObject("orgaoJulgador");
				OrgaoJulgador orgaoJulgador = new OrgaoJulgador();
				orgaoJulgador.setCodigoOrgaoJulgador(orgao.optInt("codigo"));
				orgaoJulgador.setNomeOrgaoJulgador(orgao.optString("nome"));
				orgaoJulgador.setCodigoMunicipioIBGE(orgao.optInt("codigoMunicipioIBGE", 0));
				processoExistente.setOrgaoJulgador(orgaoJulgador);
			}

			// Assuntos
			if (source.has("assuntos")) {
				JSONArray assuntos = source.getJSONArray("assuntos");

				processoExistente.getAssuntos().clear();

				for (int i = 0; i < assuntos.length(); i++) {
					JSONObject a = assuntos.getJSONObject(i);
					Assunto assunto = new Assunto();
					assunto.setCodigo(a.optInt("codigo"));
					assunto.setNome(a.optString("nome"));
					assunto.setProcesso(processoExistente);
					processoExistente.getAssuntos().add(assunto);
				}

			}

			// Movimentos
			if (source.has("movimentos")) {
				JSONArray movimentos = source.getJSONArray("movimentos");

				processoExistente.getMovimentos().clear();

				for (int i = 0; i < movimentos.length(); i++) {
					JSONObject m = movimentos.getJSONObject(i);
					Movimento mov = new Movimento();
					mov.setCodigo(m.optInt("codigo"));
					mov.setNome(m.optString("nome"));
					if (m.has("dataHora")) {
						Instant dataHora = converteData(m.optString("dataHora"));
						mov.setDataHora(dataHora);
					}
					mov.setProcesso(processoExistente);
					processoExistente.getMovimentos().add(mov);
				}

			}

			processoRepository.save(processoExistente);

			return "Processo atualizado com sucesso a partir da API DataJud (" + numeroProcesso + "), com " + 10
					+ " movimentos.";

		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao importar processo completo: " + e.getMessage();
		}
	}

	private Instant converteData(String data) {

		Instant instante = null;

		try {
			if (data.contains("T")) {
				// formato ISO, ex: 2023-07-21T19:10:08.483Z
				instante = Instant.parse(data);
			} else {
				// formato compacto: 20250519171237
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
				LocalDateTime ldt = LocalDateTime.parse(data, formatter);
				instante = ldt.atZone(ZoneId.systemDefault()).toInstant();
			}

		} catch (Exception e) {
			System.err.println("âš ï¸� Erro ao converter data: " + data);
		}

		return instante;
	}

}