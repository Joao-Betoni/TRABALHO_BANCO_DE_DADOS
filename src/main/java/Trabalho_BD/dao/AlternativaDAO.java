
package Trabalho_BD.dao;

import Trabalho_BD.dto.QuestaoFormDTO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlternativaDAO {

    private final JdbcTemplate jdbcTemplate;

    public AlternativaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long inserirAlternativa(
            Long questaoId,
            QuestaoFormDTO.AlternativaDTO alternativa) {

        String sql = """
                INSERT INTO alternativa (questao_id, texto, correta)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                questaoId,
                alternativa.texto(),
                alternativa.correta()
        );
    }

    public List<QuestaoFormDTO.AlternativaDTO> listarPorQuestao(Long questaoId) {

        String sql = """
                SELECT texto, correta
                FROM alternativa
                WHERE questao_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(
                sql,
                (rs, numeroLinha) -> new QuestaoFormDTO.AlternativaDTO(
                        rs.getString("texto"),
                        rs.getBoolean("correta")
                ),
                questaoId
        );
    }

    public int excluirPorQuestao(Long questaoId) {

        String sql = """
                DELETE FROM alternativa
                WHERE questao_id = ?
                """;

        return jdbcTemplate.update(sql, questaoId);
    }
}
