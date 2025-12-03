package com.juridico.processos.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.juridico.processos.enums.DatajudEndpoint;
import com.juridico.processos.model.Parte;
import com.juridico.processos.model.Processo;
import com.juridico.processos.repository.ParteRepository;
import com.juridico.processos.repository.ProcessoRepository;

@Service
public class ImportarExcelProcessosService {

	private final ParteRepository parteRepository;
	private final ProcessoRepository processoRepository;
	private final DataFormatter dataFormatter = new DataFormatter();
	private final DatajudImportService datajudImportService;

	public ImportarExcelProcessosService(ParteRepository parteRepository, ProcessoRepository processoRepository,
			DatajudImportService datajudImportService) {
		this.parteRepository = parteRepository;
		this.processoRepository = processoRepository;
		this.datajudImportService = datajudImportService;
	}

	/**
	 * Importa processos a partir de um arquivo Excel.
	 *
	 * Espera colunas (na ordem): 0 - Número do processo 1 - Juizado (nome do enum
	 * ou label mapeável) 2 - Autores (identificações separadas por vírgula) 3 -
	 * Réus (identificações separadas por vírgula)
	 *
	 * Retorna uma lista de mensagens de erro, uma por linha com problema. Se a
	 * lista vier vazia, significa que todos foram importados com sucesso.
	 */
	@Transactional
	public List<String> importarExcel(MultipartFile file) {
		List<String> erros = new ArrayList<>();

		try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {

			Sheet sheet = wb.getSheetAt(0);
			if (sheet == null) {
				erros.add("Planilha vazia ou inexistente no arquivo.");
				return erros;
			}

			// linha 0 = cabeçalho
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				String numeroProcessoOriginal = getCellAsString(row, 0);
				String numeroProcesso = limparNumeroProcesso(numeroProcessoOriginal);

				String juizadoTexto = getCellAsString(row, 1);
				String autoresTexto = getCellAsString(row, 2);
				String reusTexto = getCellAsString(row, 3);

				List<String> errosLinha = new ArrayList<>();

				// valida número do processo
				if (numeroProcesso == null || numeroProcesso.isBlank()) {
					erros.add("Linha " + (i + 1) + ": número do processo vazio.");
					continue;
				}

				// verifica se já existe processo com esse número
				Optional<Processo> existente = processoRepository.findByNumeroProcesso(numeroProcesso.trim());
				if (existente.isPresent()) {
					erros.add("Linha " + (i + 1) + " (" + numeroProcesso + "): processo já cadastrado no sistema.");
					continue;
				}

				// resolve juizado a partir do texto do Excel
				DatajudEndpoint juizadoEnum = resolveJuizado(juizadoTexto);
				if (juizadoEnum == null) {
					errosLinha.add("Juizado não encontrado para: \"" + juizadoTexto + "\"");
				}

				// autores
				Set<Parte> autores = new LinkedHashSet<>();
				if (autoresTexto != null && !autoresTexto.isBlank()) {
					String[] partes = autoresTexto.split(",");
					for (String ident : partes) {
						String idLimpo = ident.trim();
						if (idLimpo.isEmpty()) {
							continue;
						}
						parteRepository.findByIdentificacao(idLimpo).ifPresentOrElse(autores::add,
								() -> errosLinha.add("Autor não encontrado: " + idLimpo));
					}
				} else {
					errosLinha.add("Nenhum autor informado.");
				}

				// réus
				Set<Parte> reus = new LinkedHashSet<>();
				if (reusTexto != null && !reusTexto.isBlank()) {
					String[] partes = reusTexto.split(",");
					for (String ident : partes) {
						String idLimpo = ident.trim();
						if (idLimpo.isEmpty()) {
							continue;
						}
						parteRepository.findByIdentificacao(idLimpo).ifPresentOrElse(reus::add,
								() -> errosLinha.add("Réu não encontrado: " + idLimpo));
					}
				} else {
					errosLinha.add("Nenhum réu informado.");
				}

				// mesma parte como autor e réu
				for (Parte a : autores) {
					if (reus.contains(a)) {
						errosLinha.add("Parte " + a.getIdentificacao() + " não pode ser Autor e Réu ao mesmo tempo.");
					}
				}

				// se houver erros, registra e pula criação desse processo
				if (!errosLinha.isEmpty()) {
					String msg = "Linha " + (i + 1) + " (" + numeroProcesso + "): " + String.join(" | ", errosLinha);
					erros.add(msg);
					continue;
				}

				// --- se chegou aqui, linha válida → cria processo ---
				Processo p = new Processo();
				p.setNumeroProcesso(numeroProcesso.trim());
				p.setJuizado(juizadoEnum);
				p.setAutores(autores);
				p.setReus(reus);

				processoRepository.save(p);

				// após salvar, importa dados completos do DataJud
				try {
				    datajudImportService.importarProcessoCompleto(juizadoEnum, p.getNumeroProcesso());
				} catch (Exception e) {
				    // não aborta a linha, mas registra erro de complemento
				    erros.add("Linha " + (i + 1) + " (" + numeroProcesso + "): "
				            + "processo criado, mas falha ao importar dados do DataJud: " + e.getMessage());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			erros.add("Erro ao processar arquivo: " + e.getMessage());
		}

		return erros;
	}

	private String getCellAsString(Row row, int index) {
		try {
			Cell cell = row.getCell(index);
			if (cell == null) {
				return null;
			}

			// Usa o formatter do POI para respeitar o formato da célula
			String valor = dataFormatter.formatCellValue(cell);
			if (valor != null) {
				valor = valor.trim();
			}
			return valor.isEmpty() ? null : valor;

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Resolve o enum DatajudEndpoint a partir do texto do Excel.
	 *
	 * Aqui estou usando o nome exato do enum (ex.: "TRF1", "JUIZADO_TRF1"). Se no
	 * futuro você quiser mapear pela descrição, pode adaptar.
	 */
	private DatajudEndpoint resolveJuizado(String texto) {
		if (texto == null || texto.isBlank()) {
			return null;
		}
		String valor = texto.trim();

		// 1) tenta bater direto com o name() do enum
		try {
			return DatajudEndpoint.valueOf(valor);
		} catch (IllegalArgumentException e) {
			// ignora, tenta abaixo
		}

		// 2) tenta ignorando caixa
		for (DatajudEndpoint ep : DatajudEndpoint.values()) {
			if (ep.name().equalsIgnoreCase(valor)) {
				return ep;
			}
		}

		// Se seu enum tiver getDescricao(), pode descomentar:
		/*
		 * for (DatajudEndpoint ep : DatajudEndpoint.values()) { if (ep.getDescricao()
		 * != null && ep.getDescricao().equalsIgnoreCase(valor)) { return ep; } }
		 */

		return null;
	}
	
	private String limparNumeroProcesso(String numero) {
	    if (numero == null) return null;
	    return numero.replaceAll("\\D", ""); // remove tudo que não é dígito
	}

}
