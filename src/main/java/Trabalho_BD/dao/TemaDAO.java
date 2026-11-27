package Trabalho_BD.dao;

import Trabalho_BD.model.Tema;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TemaDAO {

    private final JdbcTemplate jdbcTemplate;

    public TemaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Tema> mapper() {
        return (ResultSet rs, int linha) -> {
            Tema tema = new Tema();
            tema.setId(rs.getLong("id"));
            tema.setNome(rs.getString("nome"));
            return tema;
        };
    }

    public Optional<Tema> buscarPorId(Long id) {
        List<Tema> resultados = jdbcTemplate.query(
                """
                SELECT id, nome
                FROM tema
                WHERE id = ?
                """,
                mapper(), id);
        return resultados.stream().findFirst();
    }

    public Optional<Tema> buscarPorNome(String nome) {
        List<Tema> resultados = jdbcTemplate.query(
                """
                SELECT id, nome
                FROM tema
                WHERE nome = ?
                """,
                mapper(), nome);
        return resultados.stream().findFirst();
    }

    public List<Tema> listarTemas() {
        return jdbcTemplate.query(
                """
                SELECT id, nome
                FROM tema
                ORDER BY nome
                """,
                mapper());
    }

    public Long inserirTema(Tema tema) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO tema (nome) VALUES (?) RETURNING id",
                Long.class,
                tema.getNome());
    }

    public int atualizarTema(Tema tema) {
        return jdbcTemplate.update(
                "UPDATE tema SET nome = ? WHERE id = ?",
                tema.getNome(), tema.getId());
    }

    public int excluirTema(Long id) {
        return jdbcTemplate.update(
                "DELETE FROM tema WHERE id = ?",
                id);
    }
}
