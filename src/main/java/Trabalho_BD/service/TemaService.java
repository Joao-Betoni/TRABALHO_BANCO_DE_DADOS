package Trabalho_BD.service;

import Trabalho_BD.dao.TemaDAO;
import Trabalho_BD.model.Tema;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemaService {

    private final TemaDAO temaDAO;

    public TemaService(TemaDAO temaDAO) {
        this.temaDAO = temaDAO;
    }

    // os temas agrupam as questoes na tela de responder e nas consultas (RF6)
    public List<Tema> listar() {
        return temaDAO.listarTemas();
    }

    public Optional<Tema> buscarPorId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID do tema é inválido.");
        }

        return temaDAO.buscarPorId(id);
    }

    public Optional<Tema> buscarPorNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tema é obrigatório.");
        }

        return temaDAO.buscarPorNome(nome);
    }

    public Long cadastrar(Tema tema) {

        if (tema == null) {
            throw new IllegalArgumentException("Dados do tema não informados.");
        }

        if (tema.getNome() == null || tema.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do tema é obrigatório.");
        }

        if (tema.getNome().length() > 60) {
            throw new IllegalArgumentException("O nome do tema deve ter no máximo 60 caracteres.");
        }

        if (temaDAO.buscarPorNome(tema.getNome()).isPresent()) {
            throw new IllegalArgumentException("Esse tema já está cadastrado.");
        }

        return temaDAO.inserirTema(tema);
    }

    public boolean excluirPorID(Long id) {

        Optional<Tema> tema = buscarPorId(id);

        if (tema.isEmpty()) {
            return false;
        }

        return temaDAO.excluirTema(id) > 0;
    }
}
