
package Trabalho_BD.service;

import Trabalho_BD.dao.FilmeDAO;
import Trabalho_BD.dao.AvaliacaoDAO;
import Trabalho_BD.model.Filme;
import Trabalho_BD.dto.FilmeFormDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class FilmeService {

    private final FilmeDAO filmeDAO;
    private final UsuarioService usuarioService;
    private final AvaliacaoDAO avaliacaoDAO;

    public FilmeService(FilmeDAO filmeDAO,
                        UsuarioService usuarioService,
                        AvaliacaoDAO avaliacaoDAO) {
        this.filmeDAO = filmeDAO;
        this.usuarioService = usuarioService;
        this.avaliacaoDAO = avaliacaoDAO;
    }

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

        if (filme.getCadastradoPor() == null ||
                filme.getCadastradoPor() <= 0) {
            throw new IllegalArgumentException(
                    "O usuário responsável pelo cadastro é inválido."
            );
        }
    }

    public Long cadastrarFilme(Filme filme) {

        validarDadosFilme(filme);

        if (usuarioService.buscarPorId(filme.getCadastradoPor()).isEmpty()) {
            throw new IllegalArgumentException(
                    "O usuário responsável pelo cadastro não existe."
            );
        }

        boolean filmeDuplicado = filmeDAO.buscarPorTitulo(filme.getTitulo())
                .stream()
                .anyMatch(f ->
                        f.getTitulo().equals(filme.getTitulo()) &&
                                f.getAno() == filme.getAno()
                );

        if (filmeDuplicado) {
            throw new IllegalArgumentException(
                    "Esse filme já está cadastrado no catálogo."
            );
        }

        return filmeDAO.inserirFilme(filme);
    }

    @Transactional
    public Filme cadastrar(FilmeFormDTO form, Long usuarioId) {

        if (form == null) {
            throw new IllegalArgumentException(
                    "Dados do formulário não informados."
            );
        }

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "O usuário responsável pelo cadastro é inválido."
            );
        }

        Filme filme = new Filme();

        filme.setTitulo(form.titulo());
        filme.setAno(form.ano());
        filme.setGeneroId(form.generoId());
        filme.setDuracaoMin(form.duracaoMin());
        filme.setSinopse(form.sinopse());
        filme.setCadastradoPor(usuarioId);

        Long id = cadastrarFilme(filme);

        // RF4 - a MESMA transacao do cadastro do filme grava o instrumento de
        // avaliacao (1:1), o bloco padrao de questoes e o envio a todos
        Long avaliacaoId = avaliacaoDAO.inserirAvaliacao(
                id,
                "Avaliação do filme " + filme.getTitulo(),
                "Questionário por temas sobre o filme " + filme.getTitulo());
        montarBlocoPadrao(avaliacaoId);
        avaliacaoDAO.atribuirParaTodosUsuarios(avaliacaoId);

        return filmeDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalStateException(
                        "O filme foi cadastrado, mas não foi encontrado."
                ));
    }

    // bloco padrao (RF3, questoes reaproveitadas entre filmes): 1 ESCALA por
    // tema de nota + 1 ESCOLHA (Plataforma) + 1 TEXTO (Critica)
    private void montarBlocoPadrao(Long avaliacaoId) {
        List<String> temasDeNota = List.of(
                "Avaliação Geral", "Roteiro", "Direção",
                "Atuação", "Trilha Sonora");

        List<Long> questoes = new ArrayList<>();
        for (String nomeTema : temasDeNota) {
            Long questaoId = avaliacaoDAO.primeiraQuestao("ESCALA", nomeTema);
            if (questaoId != null) {
                questoes.add(questaoId);
            }
        }
        Long escolha = avaliacaoDAO.primeiraQuestao("ESCOLHA", "Plataforma");
        if (escolha != null) {
            questoes.add(escolha);
        }
        Long texto = avaliacaoDAO.primeiraQuestao("TEXTO", "Crítica");
        if (texto != null) {
            questoes.add(texto);
        }

        int ordem = 1;
        for (Long questaoId : questoes) {
            avaliacaoDAO.inserirQuestaoNoBloco(avaliacaoId, questaoId, ordem++);
        }
    }

    public Optional<Filme> buscarPorId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID do filme é inválido."
            );
        }

        return filmeDAO.buscarPorId(id);
    }

    public Filme porId(Long id) {
        return buscarPorId(id).orElse(null);
    }

    public List<Filme> buscarPorTitulo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "O título é obrigatório."
            );
        }

        return filmeDAO.buscarPorTitulo(titulo);
    }

    public List<Filme> listarFilmes() {
        return filmeDAO.listarFilmes();
    }

    public List<Filme> listarPorGenero(Long generoId) {

        if (generoId == null || generoId <= 0) {
            throw new IllegalArgumentException(
                    "O ID do gênero é inválido."
            );
        }

        return filmeDAO.listarPorGenero(generoId);
    }

    public List<Filme> listarPorUsuario(Long usuarioId) {

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "O ID do usuário é inválido."
            );
        }

        if (usuarioService.buscarPorId(usuarioId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        return filmeDAO.listarPorUsuario(usuarioId);
    }

    public List<Filme> listar(Long genero, String busca) {

        boolean temGenero = genero != null;
        boolean temBusca = busca != null && !busca.isBlank();

        if (temGenero && temBusca) {

            String termo = busca.toLowerCase(Locale.ROOT);

            return listarPorGenero(genero).stream()
                    .filter(filme ->
                            filme.getTitulo()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(termo)
                    )
                    .toList();
        }

        if (temGenero) {
            return listarPorGenero(genero);
        }

        if (temBusca) {
            return buscarPorTitulo(busca);
        }

        return listarFilmes();
    }

    public int atualizarFilme(Filme filme) {

        if (filme == null ||
                filme.getId() == null ||
                filme.getId() <= 0) {
            throw new IllegalArgumentException(
                    "O ID do filme é inválido."
            );
        }

        Filme filmeExistente = filmeDAO.buscarPorId(filme.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Filme não encontrado."
                ));

        filme.setCadastradoPor(filmeExistente.getCadastradoPor());

        validarDadosFilme(filme);

        boolean filmeDuplicado = filmeDAO.buscarPorTitulo(filme.getTitulo())
                .stream()
                .anyMatch(f ->
                        !f.getId().equals(filme.getId()) &&
                                f.getTitulo().equals(filme.getTitulo()) &&
                                f.getAno() == filme.getAno()
                );

        if (filmeDuplicado) {
            throw new IllegalArgumentException(
                    "Já existe outro filme com esse título e ano."
            );
        }

        return filmeDAO.atualizarFilme(filme);
    }

    public boolean excluirFilmePorID(Long id) {

        Optional<Filme> filme = buscarPorId(id);

        if (filme.isEmpty()) {
            return false;
        }

        int linhasAfetadas = filmeDAO.excluirFilme(id);

        return linhasAfetadas > 0;
    }

    // GET /uel/filmes/{id}/questionario - contrato que a tela filme.ts consome:
    // { avaliacaoId, filmeTitulo, questoes: [{id, enunciado, tipo, tema,
    //   escalaMin, escalaMax, obrigatoria, alternativas: [{id, texto}]}] }
    public Map<String, Object> questionarioDe(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID do filme é inválido."
            );
        }

        Map<String, Object> instrumento = avaliacaoDAO.instrumentoDoFilme(id)
                .orElse(null);

        if (instrumento == null) {
            return null;
        }

        List<Map<String, Object>> questoes = avaliacaoDAO.questoesDoFilme(id);
        for (Map<String, Object> questao : questoes) {
            questao.put("alternativas",
                    avaliacaoDAO.alternativasDeQuestao((Long) questao.get("id")));
        }

        Map<String, Object> questionario = new HashMap<>();
        questionario.put("avaliacaoId", instrumento.get("avaliacaoId"));
        questionario.put("filmeTitulo", instrumento.get("filmeTitulo"));
        questionario.put("questoes", questoes);

        return questionario;
    }
}
