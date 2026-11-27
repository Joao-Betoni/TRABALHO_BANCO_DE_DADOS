
package Trabalho_BD.service;

import Trabalho_BD.dao.GeneroDAO;
import Trabalho_BD.model.Genero;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeneroService {

    private final GeneroDAO generoDAO;

    public GeneroService(GeneroDAO generoDAO) {
        this.generoDAO = generoDAO;
    }

    private void validarDadosGenero(Genero genero) {

        if (genero == null) {
            throw new IllegalArgumentException("Dados do gênero não informados.");
        }

        if (genero.getNome() == null || genero.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do gênero é obrigatório.");
        }

        if (genero.getNome().length() > 40) {
            throw new IllegalArgumentException(
                    "O nome do gênero deve ter no máximo 40 caracteres."
            );
        }
    }

    public Long cadastrarGenero(Genero genero) {

        validarDadosGenero(genero);

        if (generoDAO.buscarPorNome(genero.getNome()).isPresent()) {
            throw new IllegalArgumentException(
                    "Esse gênero já está cadastrado."
            );
        }

        return generoDAO.inserirGenero(genero);
    }

    public Optional<Genero> buscarPorId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID do gênero é inválido.");
        }

        return generoDAO.buscarPorId(id);
    }

    public Optional<Genero> buscarPorNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do gênero é obrigatório.");
        }

        return generoDAO.buscarPorNome(nome);
    }

    public List<Genero> listarGeneros() {
        return generoDAO.listarGeneros();
    }

    public List<Genero> listar() {
        return listarGeneros();
    }

    public int atualizarGenero(Genero genero) {

        if (genero == null || genero.getId() == null || genero.getId() <= 0) {
            throw new IllegalArgumentException("O ID do gênero é inválido.");
        }

        validarDadosGenero(genero);

        if (generoDAO.buscarPorId(genero.getId()).isEmpty()) {
            throw new IllegalArgumentException("Gênero não encontrado.");
        }

        Optional<Genero> generoExistente =
                generoDAO.buscarPorNome(genero.getNome());

        if (generoExistente.isPresent() &&
                !generoExistente.get().getId().equals(genero.getId())) {
            throw new IllegalArgumentException(
                    "O nome informado já pertence a outro gênero."
            );
        }

        return generoDAO.atualizarGenero(genero);
    }

    public boolean excluirGeneroPorID(Long id) {

        Optional<Genero> genero = buscarPorId(id);

        if (genero.isEmpty()) {
            return false;
        }

        int linhasAfetadas = generoDAO.excluirGenero(id);

        return linhasAfetadas > 0;
    }
}
