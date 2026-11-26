
package Trabalho_BD.dao;

import Trabalho_BD.model.Usuario;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class usuarioDao {

    private final JdbcTemplate jdbcTemplate;

    public usuarioDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Usuario> usuarioRowMapper = (rs, numeroLinha) ->
            new Usuario(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("nome_perfil"),
                    rs.getString("email"),
                    rs.getString("senha"),
                    rs.getBoolean("admin")
            );

    // INSERT
    public Long inserirUsuario(Usuario usuario) {

        String sql = """
                INSERT INTO usuario
                       (nome, nome_perfil, email, senha, admin)
                       VALUES (?, ?, ?, ?, ?)
                       RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                usuario.getNome(),
                usuario.getNomePerfil(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.isAdmin()
        );
    }

    // SELECT POR ID
    public Optional<Usuario> buscarPorId(Long id) {

        String sql = """
                SELECT id, nome, nome_perfil, email, senha, admin
                FROM usuario
                WHERE id = ?
                """;

        List<Usuario> usuarios = jdbcTemplate.query(
                sql,
                usuarioRowMapper,
                id
        );

        return usuarios.stream().findFirst();
    }

    // SELECT POR NOME DE PERFIL
    public Optional<Usuario> buscarPorNomePerfil(String nomePerfil) {

        String sql = """
                SELECT id, nome, nome_perfil, email, senha, admin
                FROM usuario
                WHERE nome_perfil = ?
                """;

        List<Usuario> usuarios = jdbcTemplate.query(
                sql,
                usuarioRowMapper,
                nomePerfil
        );

        return usuarios.stream().findFirst();
    }

    // SELECT POR EMAIL
    public Optional<Usuario> buscarPorEmail(String email) {

        String sql = """
                SELECT id, nome, nome_perfil, email, senha, admin
                FROM usuario
                WHERE email = ?
                """;

        List<Usuario> usuarios = jdbcTemplate.query(
                sql,
                usuarioRowMapper,
                email
        );

        return usuarios.stream().findFirst();
    }

    // SELECT TODOS
    public List<Usuario> listarUsuarios() {

        String sql = """
                SELECT id, nome, nome_perfil, email, senha, admin
                FROM usuario
                ORDER BY nome
                """;

        return jdbcTemplate.query(
                sql,
                usuarioRowMapper
        );
    }

    // perfis PUBLICOS da comunidade (tela usuarios.html): nome, @perfil e
    // categoria - nunca email/senha (o perfil e publico por definicao, RF6)
    public List<Map<String, Object>> listarPerfisPublicos() {

        String sql = """
                SELECT nome,
                       nome_perfil AS "nomePerfil",
                       categoria
                FROM usuario
                ORDER BY nome
                """;

        return jdbcTemplate.queryForList(sql);
    }

    // UPDATE
    public int atualizarUsuario(Usuario usuario) {

        String sql = """
                UPDATE usuario
                SET nome = ?,
                    nome_perfil = ?,
                    email = ?
                WHERE id = ?
                """;

        return jdbcTemplate.update(
                sql,
                usuario.getNome(),
                usuario.getNomePerfil(),
                usuario.getEmail(),
                usuario.getId()
        );
    }

    // DELETE
    public int excluirUsuario(Long id) {

        String sql = """
                DELETE FROM usuario
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }
}
