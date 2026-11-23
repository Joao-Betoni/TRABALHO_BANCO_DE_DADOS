
package Trabalho_BD.dao;

import Trabalho_BD.model.Filme;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmeDAO {

    private final JdbcTemplate jdbcTemplate;

    public FilmeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CONVERSÃO DOS DADOS DO POSTGRESQL PARA OBJETOS JAVA
    private final RowMapper<Filme> filmeRowMapper = (rs, numeroLinha) -> {

        Filme filme = new Filme();

        filme.setId(rs.getLong("id"));
        filme.setTitulo(rs.getString("titulo"));
        filme.setAno(rs.getInt("ano"));
        filme.setGeneroId(rs.getLong("genero_id"));
        filme.setDuracaoMin(rs.getObject("duracao_min", Integer.class));
        filme.setSinopse(rs.getString("sinopse"));
        filme.setCadastradoPor(rs.getLong("cadastrado_por"));
        filme.setCadastradoEm(
                rs.getObject("cadastrado_em", java.time.LocalDateTime.class)
        );

        return filme;
    };

    // INSERT
    public Long inserirFilme(Filme filme) {

        String sql = """
                INSERT INTO filme
                    (titulo, ano, genero_id, duracao_min, sinopse, cadastrado_por)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                filme.getTitulo(),
                filme.getAno(),
                filme.getGeneroId(),
                filme.getDuracaoMin(),
                filme.getSinopse(),
                filme.getCadastradoPor()
        );
    }

    // SELECT POR ID
    public Optional<Filme> buscarPorId(Long id) {

        String sql = """
                SELECT id, titulo, ano, genero_id, duracao_min,
                       sinopse, cadastrado_por, cadastrado_em
                FROM filme
                WHERE id = ?
                """;

        List<Filme> filmes = jdbcTemplate.query(
                sql,
                filmeRowMapper,
                id
        );

        return filmes.stream().findFirst();
    }

    // SELECT POR TÍTULO
    public List<Filme> buscarPorTitulo(String titulo) {

        String sql = """
                SELECT id, titulo, ano, genero_id, duracao_min,
                       sinopse, cadastrado_por, cadastrado_em
                FROM filme
                WHERE titulo ILIKE ?
                ORDER BY titulo, ano
                """;

        return jdbcTemplate.query(
                sql,
                filmeRowMapper,
                "%" + titulo + "%"
        );
    }

    // SELECT TODOS
    public List<Filme> listarFilmes() {

        String sql = """
                SELECT id, titulo, ano, genero_id, duracao_min,
                       sinopse, cadastrado_por, cadastrado_em
                FROM filme
                ORDER BY titulo
                """;

        return jdbcTemplate.query(
                sql,
                filmeRowMapper
        );
    }

    // SELECT POR GÊNERO
    public List<Filme> listarPorGenero(Long generoId) {

        String sql = """
                SELECT id, titulo, ano, genero_id, duracao_min,
                       sinopse, cadastrado_por, cadastrado_em
                FROM filme
                WHERE genero_id = ?
                ORDER BY titulo
                """;

        return jdbcTemplate.query(
                sql,
                filmeRowMapper,
                generoId
        );
    }

    // SELECT POR USUÁRIO QUE CADASTROU
    public List<Filme> listarPorUsuario(Long usuarioId) {

        String sql = """
                SELECT id, titulo, ano, genero_id, duracao_min,
                       sinopse, cadastrado_por, cadastrado_em
                FROM filme
                WHERE cadastrado_por = ?
                ORDER BY cadastrado_em DESC
                """;

        return jdbcTemplate.query(
                sql,
                filmeRowMapper,
                usuarioId
        );
    }

    // UPDATE
    public int atualizarFilme(Filme filme) {

        String sql = """
                UPDATE filme
                SET titulo = ?,
                    ano = ?,
                    genero_id = ?,
                    duracao_min = ?,
                    sinopse = ?
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                filme.getTitulo(),
                filme.getAno(),
                filme.getGeneroId(),
                filme.getDuracaoMin(),
                filme.getSinopse(),
                filme.getId()
        );
    }

    // DELETE
    public int excluirFilme(Long id) {

        String sql = """
                DELETE FROM filme
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }
}
