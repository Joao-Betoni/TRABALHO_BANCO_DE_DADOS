package Trabalho_BD.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RelatorioDAO {

    private final JdbcTemplate jdbcTemplate;

    public RelatorioDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // os SQL abaixo espelham database/relatorios.sql (mudou de um lado, muda no outro)

    // R1 - media por tema x genero (JOIN, GROUP BY/HAVING, subconsulta IN)
    public List<Map<String, Object>> temasPorGenero(String nomeGeneros) {
        String filtro = """
                WHERE f.genero_id IN (
                    SELECT id FROM genero
                    WHERE nome = ANY(string_to_array(?, ','))
                )""";

        String sql = """
                SELECT g.nome                AS genero,
                       t.nome                AS tema,
                       ROUND(AVG(r.nota), 2) AS media,
                       COUNT(*)              AS respostas
                FROM resposta r
                JOIN avaliacao_usuario au ON au.id = r.avaliacao_usuario_id
                JOIN avaliacao a          ON a.id  = au.avaliacao_id
                JOIN filme f              ON f.id  = a.filme_id
                JOIN genero g             ON g.id  = f.genero_id
                JOIN questao q            ON q.id  = r.questao_id AND q.tipo = 'ESCALA'
                JOIN tema t               ON t.id  = q.tema_id
                """ + (nomeGeneros == null || nomeGeneros.isBlank() ? "" : filtro)
                + " GROUP BY g.nome, t.nome HAVING COUNT(*) >= 5 ORDER BY g.nome, media DESC";

        return nomeGeneros == null || nomeGeneros.isBlank()
                ? jdbcTemplate.queryForList(sql)
                : jdbcTemplate.queryForList(sql, nomeGeneros);
    }

    // R2 - top 10 filmes (subconsulta da media geral, RANK(), FETCH FIRST, ILIKE)
    public List<Map<String, Object>> top10(String busca) {
        return jdbcTemplate.queryForList(
                """
                SELECT RANK() OVER (ORDER BY m.media DESC) AS posicao,
                       m.titulo,
                       m.ano,
                       m.genero,
                       ROUND(m.media, 2)                    AS media,
                       m.avaliadores                        AS avaliadores
                FROM (
                    SELECT f.titulo, f.ano, g.nome AS genero,
                           AVG(r.nota)             AS media,
                           COUNT(DISTINCT au.id)   AS avaliadores
                    FROM resposta r
                    JOIN avaliacao_usuario au ON au.id = r.avaliacao_usuario_id
                    JOIN avaliacao a          ON a.id  = au.avaliacao_id
                    JOIN filme f              ON f.id  = a.filme_id
                    JOIN genero g             ON g.id  = f.genero_id
                    WHERE f.titulo ILIKE ? || '%'
                    GROUP BY f.id, f.titulo, f.ano, g.nome
                    HAVING COUNT(DISTINCT au.id) >= 3
                ) m
                """
                + "\nORDER BY posicao FETCH FIRST 10 ROWS ONLY",
                busca == null ? "" : busca);
    }

    // R3a - filmes com menos resposta (LEFT JOIN: o ZERO tem que aparecer)
    public List<Map<String, Object>> filmesSemAvaliacao() {
        return jdbcTemplate.queryForList(
                """
                SELECT f.titulo,
                       COUNT(DISTINCT au.id) AS avaliadores,
                       COUNT(r.id)           AS respostas
                FROM filme f
                LEFT JOIN avaliacao          a  ON a.filme_id = f.id
                LEFT JOIN avaliacao_usuario au ON au.avaliacao_id = a.id
                LEFT JOIN resposta           r  ON r.avaliacao_usuario_id = au.id
                GROUP BY f.id, f.titulo
                ORDER BY avaliadores ASC, respostas ASC
                FETCH FIRST 10 ROWS ONLY
                """);
    }

    // R3b - temas e questoes que ninguem respondeu (RIGHT JOIN)
    public List<Map<String, Object>> temasSemResposta() {
        return jdbcTemplate.queryForList(
                """
                SELECT t.nome      AS tema,
                       COUNT(q.id) AS questoes,
                       COUNT(r.id) AS respostas
                FROM resposta r
                RIGHT JOIN questao q ON q.id = r.questao_id
                RIGHT JOIN tema t    ON t.id = q.tema_id
                GROUP BY t.nome
                ORDER BY respostas ASC
                """);
    }

    // R4 - evolucao semanal das notas por grupo (date_trunc, periodo, IN)
    public List<Map<String, Object>> evolucao(List<String> categorias,
                                              String inicio, String fim) {
        String filtro = categorias == null || categorias.isEmpty()
                ? ""
                : " AND u.categoria = ANY(string_to_array(?, ','))";

        String sql = """
                SELECT date_trunc('week', au.enviada_em)::date AS semana,
                       t.nome                                  AS tema,
                       u.categoria                             AS grupo,
                       ROUND(AVG(r.nota), 2)                   AS media,
                       COUNT(*)                                AS respostas
                FROM resposta r
                JOIN avaliacao_usuario au ON au.id = r.avaliacao_usuario_id
                JOIN usuario u            ON u.id  = au.usuario_id
                JOIN questao q            ON q.id  = r.questao_id AND q.tipo = 'ESCALA'
                JOIN tema t               ON t.id  = q.tema_id
                WHERE au.enviada_em >= ?::timestamp
                  AND au.enviada_em <= ?::timestamp
                """
                + filtro
                + "\n GROUP BY semana, t.nome, grupo\n ORDER BY semana, tema";

        if (filtro.isEmpty()) {
            return jdbcTemplate.queryForList(sql, inicio, fim);
        }
        return jdbcTemplate.queryForList(sql, inicio, fim,
                categorias == null ? null : String.join(",", categorias));
    }

    // R5 - temas mais criticados: % de notas 1 e 2 (FILTER, HAVING, RANK())
    public List<Map<String, Object>> temasMaisCriticados() {
        return jdbcTemplate.queryForList(
                """
                SELECT RANK() OVER (ORDER BY ROUND(100.0 * COUNT(*) FILTER (WHERE r.nota BETWEEN 1 AND 2)
                                                     / COUNT(*), 1) DESC) AS posicao,
                       t.nome                                     AS tema,
                       ROUND(AVG(r.nota), 2)                      AS media,
                       ROUND(100.0 * COUNT(*) FILTER (WHERE r.nota BETWEEN 1 AND 2)
                             / COUNT(*), 1)                       AS "pctCriticadas",
                       COUNT(*)                                   AS respostas
                FROM resposta r
                JOIN questao q ON q.id = r.questao_id AND q.tipo = 'ESCALA'
                JOIN tema t    ON t.id = q.tema_id
                GROUP BY t.nome
                HAVING COUNT(*) >= 10
                ORDER BY posicao
                FETCH FIRST 5 ROWS ONLY
                """);
    }
}
