
package Trabalho_BD.service;

import Trabalho_BD.model.Usuario;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
public class PerfilService {

    private final UsuarioService usuarioService;
    private final JdbcTemplate jdbcTemplate;

    public PerfilService(UsuarioService usuarioService,
                         JdbcTemplate jdbcTemplate) {

        this.usuarioService = usuarioService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> buscarPorNomePerfil(String nomePerfil) {

        if (nomePerfil == null || nomePerfil.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome de perfil é obrigatório."
            );
        }

        Optional<Usuario> usuario =
                usuarioService.buscarPorNomePerfil(nomePerfil);

        if (usuario.isEmpty()) {
            return null;
        }

        Usuario usuarioEncontrado = usuario.get();

        String sql = """
                SELECT DISTINCT
                    f.id,
                    f.titulo,
                    f.ano
                FROM filme f
                INNER JOIN avaliacao a
                    ON a.filme_id = f.id
                INNER JOIN avaliacao_usuario au
                    ON au.avaliacao_id = a.id
                WHERE au.usuario_id = ?
                  AND au.anonima = FALSE
                ORDER BY f.titulo, f.ano
                """;

        List<Map<String, Object>> filmesAvaliados =
                jdbcTemplate.queryForList(
                        sql,
                        usuarioEncontrado.getId()
                );

        Map<String, Object> perfil = new LinkedHashMap<>();

        perfil.put("nome", usuarioEncontrado.getNome());
        perfil.put("nomePerfil", usuarioEncontrado.getNomePerfil());
        perfil.put("filmesAvaliados", filmesAvaliados);

        return perfil;
    }
}
