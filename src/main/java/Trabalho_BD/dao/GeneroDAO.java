
package Trabalho_BD.dao;

import Trabalho_BD.model.Genero;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GeneroDAO {

    private final JdbcTemplate jdbcTemplate;

    public GeneroDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Genero> generoRowMapper = (rs, numeroLinha) -> {
        Genero genero = new Genero();

        genero.setId(rs.getLong("id"));
        genero.setNome(rs.getString("nome"));

        return genero;
    };

    public Long inserirGenero(Genero genero) {

        String sql = """
                INSERT INTO genero (nome)
                VALUES (?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                genero.getNome()
        );
    }

    public Optional<Genero> buscarPorId(Long id) {

        String sql = """
                SELECT id, nome
                FROM genero
                WHERE id = ?
                """;

        List<Genero> generos = jdbcTemplate.query(
                sql,
                generoRowMapper,
                id
        );

        return generos.stream().findFirst();
    }

    public Optional<Genero> buscarPorNome(String nome) {

        String sql = """
                SELECT id, nome
                FROM genero
                WHERE nome = ?
                """;

        List<Genero> generos = jdbcTemplate.query(
                sql,
                generoRowMapper,
                nome
        );

        return generos.stream().findFirst();
    }

    public List<Genero> listarGeneros() {

        String sql = """
                SELECT id, nome
                FROM genero
                ORDER BY nome
                """;

        return jdbcTemplate.query(
                sql,
                generoRowMapper
        );
    }

    public int atualizarGenero(Genero genero) {

        String sql = """
                UPDATE genero
                SET nome = ?
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                genero.getNome(),
                genero.getId()
        );
    }

    public int excluirGenero(Long id) {

        String sql = """
                DELETE FROM genero
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }
}
