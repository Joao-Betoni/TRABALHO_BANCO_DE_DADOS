package Trabalho_BD.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RespostaDAO {

    private final JdbcTemplate jdbcTemplate;

    public RespostaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // INSERT avaliacao_usuario (a "tentativa") - UNICA por usuario/instrumento (RF5)
    public Long inserirTentativa(Long avaliacaoId, Long usuarioId, boolean anonima) {
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO avaliacao_usuario (avaliacao_id, usuario_id, enviada_em, anonima)
                VALUES (?, ?, now(), ?)
                RETURNING id
                """,
                Long.class,
                avaliacaoId, usuarioId, anonima);
    }

    // INSERT das respostas em lote - no mesmo commit da tentativa
    public int[] inserirRespostas(Long avaliacaoUsuarioId, List<Object[]> linhas) {
        String sql = """
                INSERT INTO resposta (avaliacao_usuario_id, questao_id, nota, alternativa_id, texto)
                VALUES (?, ?, ?, ?, ?)
                """;
        List<Object[]> comTentativa = linhas.stream()
                .map((Object[] linha) -> new Object[]{avaliacaoUsuarioId,
                        linha[0], linha[1], linha[2], linha[3]})
                .toList();
        return jdbcTemplate.batchUpdate(sql, comTentativa);
    }

    public boolean usuarioJaRespondeu(Long avaliacaoId, Long usuarioId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM avaliacao_usuario WHERE avaliacao_id = ? AND usuario_id = ?",
                Integer.class, avaliacaoId, usuarioId);
        return total != null && total > 0;
    }

    // a questao precisa pertencer ao instrumento respondido (evita resposta "fora")
    public Optional<Map<String, Object>> questaoDeAvaliacao(Long avaliacaoId, Long questaoId) {
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                """
                SELECT q.id,
                       q.enunciado,
                       q.tipo,
                       q.escala_min      AS "escalaMin",
                       q.escala_max      AS "escalaMax",
                       q.obrigatoria      AS obrigatoria
                FROM avaliacao_questao aq
                JOIN questao q ON q.id = aq.questao_id
                WHERE aq.avaliacao_id = ? AND q.id = ?
                """,
                avaliacaoId, questaoId);
        return resultado.stream().findFirst();
    }

    // a alternativa escolhida precisa ser da propria questao
    public boolean alternativaPertenceQuestao(Long questaoId, Long alternativaId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alternativa WHERE id = ? AND questao_id = ?",
                Integer.class, alternativaId, questaoId);
        return total != null && total > 0;
    }

    // GET /uel/avaliacoes/me: pendentes (LEFT JOIN -> sem tentativa) e respondidas
    public List<Map<String, Object>> listarAvaliacoesDoUsuario(Long usuarioId) {
        return jdbcTemplate.queryForList(
                """
                SELECT a.id            AS "avaliacaoId",
                       f.titulo        AS titulo,
                       f.ano           AS ano,
                       au.id           AS "avaliacaoUsuarioId",
                       au.enviada_em   AS "enviadaEm",
                       au.anonima      AS anonima
                FROM avaliacao a
                JOIN filme f                ON f.id = a.filme_id
                LEFT JOIN avaliacao_usuario au ON au.avaliacao_id = a.id AND au.usuario_id = ?
                ORDER BY f.titulo, f.ano
                """,
                usuarioId);
    }
}
