
package Trabalho_BD.service;

import Trabalho_BD.dao.FilmeDAO;
import Trabalho_BD.model.Filme;
import Trabalho_BD.model.Usuario;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmeService {

    private final FilmeDAO filmeDAO;
    private final UsuarioService usuarioService;

    public FilmeService(FilmeDAO filmeDAO, UsuarioService usuarioService) {
        this.filmeDAO = filmeDAO;
        this.usuarioService = usuarioService;
    }

    // VALIDAÇÃO DOS DADOS DO FILME
    private void validarDadosFilme(Filme filme) {

        if (filme == null) {
            throw new IllegalArgumentException("Dados do filme não informados.");
        }

        if (filme.getTitulo() == null || filme.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }

        if (filme.getTitulo().length() > 150) {
            throw new IllegalArgumentException(
                    "O título deve ter no máximo 150 caracteres."
            );
        }

        if (filme.getAno() < 1895 || filme.getAno() > 2100) {
            throw new IllegalArgumentException(
                    "O ano do filme deve estar entre 1895 e 2100."
            );
        }

        if (filme.getGeneroId() == null || filme.getGeneroId() <= 0) {
            throw new IllegalArgumentException("O gênero informado é inválido.");
        }

        if (filme.getDuracaoMin() != null &&
                (filme.getDuracaoMin() <= 0 || filme.getDuracaoMin() > 32767)) {
            throw new IllegalArgumentException(
                    "A duração deve estar entre 1 e 32767 minutos."
            );
        }

        if (filme.getCadastradoPor() == null || filme.getCadastradoPor() <= 0) {
            throw new IllegalArgumentException(
                    "O usuário responsável pelo cadastro é inválido."
            );
        }
    }

    // CADASTRAR FILME
    public Long cadastrarFilme(Filme filme) {

        validarDadosFilme(filme);

        Optional<Usuario> usuario =
                usuarioService.buscarPorId(filme.getCadastradoPor());

        if (usuario.isEmpty()) {
            throw new IllegalArgumentException(
                    "O usuário responsável pelo cadastro não existe."
            );
        }

        return filmeDAO.inserirFilme(filme);
    }

    // BUSCAR FILME POR ID
    public Optional<Filme> buscarPorId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID do filme é inválido.");
        }

        return filmeDAO.buscarPorId(id);
    }

    // BUSCAR FILMES POR TÍTULO
    public List<Filme> buscarPorTitulo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }

        return filmeDAO.buscarPorTitulo(titulo);
    }

    // LISTAR TODOS OS FILMES
    public List<Filme> listarFilmes() {

        return filmeDAO.listarFilmes();
    }

    // LISTAR FILMES POR GÊNERO
    public List<Filme> listarPorGenero(Long generoId) {

        if (generoId == null || generoId <= 0) {
            throw new IllegalArgumentException("O ID do gênero é inválido.");
        }

        return filmeDAO.listarPorGenero(generoId);
    }

    // LISTAR FILMES POR USUÁRIO
    public List<Filme> listarPorUsuario(Long usuarioId) {

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário é inválido.");
        }

        Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioId);

        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        return filmeDAO.listarPorUsuario(usuarioId);
    }

    // ATUALIZAR FILME
    public int atualizarFilme(Filme filme) {

        validarDadosFilme(filme);

        if (filme.getId() == null || filme.getId() <= 0) {
            throw new IllegalArgumentException("O ID do filme é inválido.");
        }

        Optional<Filme> filmeExistente = filmeDAO.buscarPorId(filme.getId());

        if (filmeExistente.isEmpty()) {
            throw new IllegalArgumentException("Filme não encontrado.");
        }

        return filmeDAO.atualizarFilme(filme);
    }

    // EXCLUIR FILME
    public boolean excluirFilmePorID(Long id) {

        Optional<Filme> filme = buscarPorId(id);

        if (filme.isEmpty()) {
            return false;
        }

        int linhasAfetadas = filmeDAO.excluirFilme(id);

        return linhasAfetadas > 0;
    }
}
