package Trabalho_BD.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AvaliacaoDAO {

    private final JdbcTemplate jdbcTemplate;

    public AvaliacaoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // o instrumento nasce junto com o filme (1:1, RF1) DENTRO da transacao do cadastro
    public Long inserirAvaliacao(Long filmeId, String titulo, String descricao) {
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO avaliacao (filme_id, titulo, descricao, data_inicio)
                VALUES (?, ?, ?, now())
                RETURNING id
                """,
                Long.class,
                filmeId, titulo, descricao);
    }

    public Optional<Map<String, Object>> buscarPorId(Long id) {
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                """
                SELECT id, filme_id, titulo, descricao, data_inicio, data_fim
                FROM avaliacao
                WHERE id = ?
                """,
                id);
        return resultado.stream().findFirst();
    }

    // RF4: o instrumento vai para TODOS os usuarios na mesma transacao
    public int atribuirParaTodosUsuarios(Long avaliacaoId) {
        return jdbcTemplate.update(
                """
                INSERT INTO avaliacao_destinatario (avaliacao_id, usuario_id)
                SELECT ?, id FROM usuario
                """,
                avaliacaoId);
    }

    public void inserirQuestaoNoBloco(Long avaliacaoId, Long questaoId, int ordem) {
        jdbcTemplate.update(
                "INSERT INTO avaliacao_questao (avaliacao_id, questao_id, ordem) VALUES (?, ?, ?)",
                avaliacaoId, questaoId, ordem);
    }

    // primeira questao de um tipo/tema (menor id) - e' a que entra no bloco padrao
    public Long primeiraQuestao(String tipo, String nomeTema) {
        List<Long> resultado = jdbcTemplate.queryForList(
                """
                SELECT MIN(q.id)
                FROM questao q
                JOIN tema t ON t.id = q.tema_id
                WHERE q.tipo = ? AND t.nome = ?
                """,
                Long.class, tipo, nomeTema);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    // GET /uel/filmes/{id}/questionario: questoes do instrumento, na ordem da tela
    public List<Map<String, Object>> questoesDoFilme(Long filmeId) {
        return jdbcTemplate.queryForList(
                """
                SELECT q.id,
                       q.enunciado,
                       q.tipo,
                       t.nome            AS tema,
                       q.escala_min      AS "escalaMin",
                       q.escala_max      AS "escalaMax",
                       q.obrigatoria     AS obrigatoria
                FROM avaliacao a
                JOIN avaliacao_questao aq ON aq.avaliacao_id = a.id
                JOIN questao q            ON q.id = aq.questao_id
                JOIN tema t               ON t.id = q.tema_id
                WHERE a.filme_id = ?
                ORDER BY aq.ordem
                """,
                filmeId);
    }

    public List<Map<String, Object>> alternativasDeQuestao(Long questaoId) {
        return jdbcTemplate.queryForList(
                """
                SELECT id, texto
                FROM alternativa
                WHERE questao_id = ?
                ORDER BY id
                """,
                questaoId);
    }

    // dados do instrumento + nome do filme (monta o JSON do questionario)
    public Optional<Map<String, Object>> instrumentoDoFilme(Long filmeId) {
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                """
                SELECT a.id        AS "avaliacaoId",
                       f.titulo    AS "filmeTitulo"
                FROM avaliacao a
                JOIN filme f ON f.id = a.filme_id
                WHERE a.filme_id = ?
                """,
                filmeId);
        return resultado.stream().findFirst();
    }
}
