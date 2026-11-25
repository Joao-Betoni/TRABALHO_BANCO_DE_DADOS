package Trabalho_BD.service;

import Trabalho_BD.dao.RelatorioDAO;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    private final RelatorioDAO relatorioDAO;

    public RelatorioService(RelatorioDAO relatorioDAO) {
        this.relatorioDAO = relatorioDAO;
    }

    private static final List<String> TODAS_CATEGORIAS =
            List.of("ESPECTADOR", "CINEFILO", "CRITICO");

    // R1 - ?generos=Drama,Comédia (sem parametro: todos os generos)
    public List<Map<String, Object>> temasPorGenero(String generos) {
        return relatorioDAO.temasPorGenero(generos);
    }

    // R2 - ?busca=deus (ILIKE na subconsulta)
    public List<Map<String, Object>> top10(String busca) {
        return relatorioDAO.top10(busca);
    }

    // R3 - filmes sem avaliacao (LEFT JOIN); o "temasSemResposta" (RIGHT JOIN)
    // fica disponivel para a mesma aba quando a dupla quiser as duas tabelas
    public List<Map<String, Object>> semAvaliacao() {
        return relatorioDAO.filmesSemAvaliacao();
    }

    public List<Map<String, Object>> temasSemResposta() {
        return relatorioDAO.temasSemResposta();
    }

    // R4 - ?categorias=ESPECTADOR,CINEFILO&inicio=2026-10-01&fim=2026-11-12
    public List<Map<String, Object>> evolucao(String categorias, String inicio, String fim) {

        List<String> listaCategorias = null;
        if (categorias != null && !categorias.isBlank()) {
            listaCategorias = Arrays.stream(categorias.split(","))
                    .map(String::trim)
                    .filter((String c) -> !c.isEmpty())
                    .toList();
            for (String categoria : listaCategorias) {
                if (!TODAS_CATEGORIAS.contains(categoria)) {
                    throw new IllegalArgumentException(
                            "Categoria desconhecida: " + categoria);
                }
            }
        }

        // sem datas na tela: ultimas 10 semanas (padrao do relatorios.sql)
        String inicioEfetivo = inicio == null || inicio.isBlank()
                ? LocalDateTime.now().minusWeeks(10).withNano(0).toString()
                : normalizaData(inicio);
        String fimEfetivo = fim == null || fim.isBlank()
                ? Instant.now().atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime().withNano(0).toString()
                : normalizaData(fim);

        return relatorioDAO.evolucao(listaCategorias, inicioEfetivo, fimEfetivo);
    }

    // aceita "2026-10-01" (só data, vira 00:00) ou datetime ISO completo
    private String normalizaData(String valor) {
        try {
            return LocalDateTime.parse(valor).toString();
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(valor).atStartOfDay().toString();
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Data invalida no filtro: " + valor);
            }
        }
    }

    // R5 - temas mais criticados (sem parametro)
    public List<Map<String, Object>> maisCriticados() {
        return relatorioDAO.temasMaisCriticados();
    }
}
