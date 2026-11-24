
package Trabalho_BD.dao;

import Trabalho_BD.model.Questao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuestaoDAO {

    private final JdbcTemplate jdbcTemplate;

    public QuestaoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Questao> questaoRowMapper = (rs, numeroLinha) -> {

        Questao questao = new Questao();

        questao.setId(rs.getLong("id"));
        questao.setEnunciado(rs.getString("enunciado"));
        questao.setTipo(rs.getString("tipo"));
        questao.setTemaId(rs.getObject("tema_id", Long.class));
        questao.setEscalaMin(rs.getObject("escala_min", Integer.class));
        questao.setEscalaMax(rs.getObject("escala_max", Integer.class));
        questao.setObrigatoria(rs.getBoolean("obrigatoria"));

        return questao;
    };

    public Long inserirQuestao(Questao questao) {

        String sql = """
                INSERT INTO questao
                    (enunciado, tipo, tema_id, escala_min, escala_max, obrigatoria)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                questao.getEnunciado(),
                questao.getTipo(),
                questao.getTemaId(),
                questao.getEscalaMin(),
                questao.getEscalaMax(),
                questao.isObrigatoria()
        );
    }

    public Optional<Questao> buscarPorId(Long id) {

        String sql = """
                SELECT id, enunciado, tipo, tema_id,
                       escala_min, escala_max, obrigatoria
                FROM questao
                WHERE id = ?
                """;

        List<Questao> questoes = jdbcTemplate.query(
                sql,
                questaoRowMapper,
                id
        );

        return questoes.stream().findFirst();
    }

    public List<Questao> listarQuestoes() {

        String sql = """
                SELECT id, enunciado, tipo, tema_id,
                       escala_min, escala_max, obrigatoria
                FROM questao
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                questaoRowMapper
        );
    }

    public List<Questao> listarPorTipo(String tipo) {

        String sql = """
                SELECT id, enunciado, tipo, tema_id,
                       escala_min, escala_max, obrigatoria
                FROM questao
                WHERE tipo = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                questaoRowMapper,
                tipo
        );
    }

    public List<Questao> listarPorTema(Long temaId) {

        String sql = """
                SELECT id, enunciado, tipo, tema_id,
                       escala_min, escala_max, obrigatoria
                FROM questao
                WHERE tema_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                questaoRowMapper,
                temaId
        );
    }

    public int atualizarQuestao(Questao questao) {

        String sql = """
                UPDATE questao
                SET enunciado = ?,
                    tipo = ?,
                    tema_id = ?,
                    escala_min = ?,
                    escala_max = ?,
                    obrigatoria = ?
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                questao.getEnunciado(),
                questao.getTipo(),
                questao.getTemaId(),
                questao.getEscalaMin(),
                questao.getEscalaMax(),
                questao.isObrigatoria(),
                questao.getId()
        );
    }

    public int excluirQuestao(Long id) {

        String sql = """
                DELETE FROM questao
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }
}
