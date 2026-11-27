package Trabalho_BD.service;

import Trabalho_BD.dao.AvaliacaoDAO;
import Trabalho_BD.dao.RespostaDAO;
import Trabalho_BD.dto.FormularioRespostaDTO;
import Trabalho_BD.dto.RespostaQuestaoDTO;
import Trabalho_BD.model.Usuario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class RespostaService {

    private final RespostaDAO respostaDAO;
    private final AvaliacaoDAO avaliacaoDAO;

    public RespostaService(RespostaDAO respostaDAO, AvaliacaoDAO avaliacaoDAO) {
        this.respostaDAO = respostaDAO;
        this.avaliacaoDAO = avaliacaoDAO;
    }

    private static final Pattern SO_BRAANCOS = Pattern.compile("\\s*");

    // transacao RF5: valida TUDO antes de gravar; INSERT avaliacao_usuario +
    // batchUpdate das respostas saem juntos no commit (ou nada saiu)
    @Transactional
    public void registrar(Long avaliacaoId, Long usuarioId, FormularioRespostaDTO form) {

        if (avaliacaoId == null || avaliacaoId <= 0) {
            throw new IllegalArgumentException("A avaliação é inválida.");
        }

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O usuário é inválido.");
        }

        if (avaliacaoDAO.buscarPorId(avaliacaoId).isEmpty()) {
            throw new IllegalArgumentException("Avaliação não encontrada.");
        }

        if (form == null || form.respostas() == null || form.respostas().isEmpty()) {
            throw new IllegalArgumentException(
                    "O questionário enviado está vazio.");
        }

        if (respostaDAO.usuarioJaRespondeu(avaliacaoId, usuarioId)) {
            throw new IllegalArgumentException(
                    "Você já respondeu a esse questionário.");
        }

        // 1) valida cada resposta ANTES de inserir qualquer linha
        List<Object[]> linhas = validarRespostas(avaliacaoId, form.respostas());

        // 2) grava a tentativa e as respostas na mesma transacao
        Long tentativaId = respostaDAO.inserirTentativa(
                avaliacaoId, usuarioId, form.anonima());

        respostaDAO.inserirRespostas(tentativaId, linhas);
    }

    private List<Object[]> validarRespostas(Long avaliacaoId, List<RespostaQuestaoDTO> enviadas) {

        List<Object[]> linhas = new ArrayList<>();

        for (RespostaQuestaoDTO enviada : enviadas) {

            if (enviada.questaoId() == null || enviada.questaoId() <= 0) {
                throw new IllegalArgumentException(
                        "Há resposta com questão inválida.");
            }

            Map<String, Object> questao = respostaDAO
                    .questaoDeAvaliacao(avaliacaoId, enviada.questaoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "A questão " + enviada.questaoId()
                                    + " não pertence a esse questionário."));

            String enunciado = (String) questao.get("enunciado");
            String tipo = (String) questao.get("tipo");
            int escalaMin = ((Number) questao.get("escalaMin")).intValue();
            int escalaMax = ((Number) questao.get("escalaMax")).intValue();
            boolean obrigatoria = (Boolean) questao.get("obrigatoria");

            boolean semNota = enviada.nota() == null;
            boolean semAlternativa = enviada.alternativaId() == null;
            boolean semTexto = enviada.texto() == null
                    || SO_BRAANCOS.matcher(enviada.texto()).matches();

            // XOR: nota OU alternativa OU texto - nunca mais de uma forma
            int preenchidas = (semNota ? 0 : 1) + (semAlternativa ? 0 : 1) + (semTexto ? 0 : 1);
            if (preenchidas > 1) {
                throw new IllegalArgumentException(
                        "A questão '" + enunciado + "' aceita apenas um tipo de resposta.");
            }

            switch (tipo) {
                case "ESCALA" -> {
                    if (!semNota && (enviada.nota() < escalaMin || enviada.nota() > escalaMax)) {
                        throw new IllegalArgumentException(
                                "A nota da questão '" + enunciado + "' deve ser de "
                                        + escalaMin + " a " + escalaMax + ".");
                    }
                }
                case "ESCOLHA" -> {
                    if (!semAlternativa && !respostaDAO.alternativaPertenceQuestao(
                            enviada.questaoId(), enviada.alternativaId())) {
                        throw new IllegalArgumentException(
                                "A alternativa informada não é dessa questão.");
                    }
                }
                case "TEXTO" -> { }
                default -> throw new IllegalArgumentException(
                        "Tipo de questão desconhecido: " + tipo);
            }

            boolean respondida = preenchidas > 0;

            if (!respondida && obrigatoria) {
                throw new IllegalArgumentException(
                        "A questão '" + enunciado + "' é obrigatória.");
            }

            if (!respondida) {
                continue; // opcional vazia: simplesmente nao se grava (o CHECK
                          // chk_res_uma_forma exige exatamente 1 dos 3 campos)
            }

            linhas.add(new Object[]{enviada.questaoId(), enviada.nota(),
                    enviada.alternativaId(), enviada.texto()});
        }

        if (linhas.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhuma resposta preenchida no questionário.");
        }

        return linhas;
    }

    // GET /uel/avaliacoes/me: instrumentos pendentes e respondidos
    public List<Map<String, Object>> minhasAvaliacoes(Long usuarioId) {

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O usuário é inválido.");
        }

        return respostaDAO.listarAvaliacoesDoUsuario(usuarioId);
    }

    // GET /uel/avaliacoes/usuario/{nomePerfil}: respostas detalhadas de um
    // perfil. Se o logado e o dono do perfil, ele ve as anonimas dele (RF6);
    // qualquer outro ve somente as publicas. Formato por filme:
    // { avaliacaoId, filmeId, titulo, ano, enviadaEm, anonima, respostas: [...] }
    public List<Map<String, Object>> avaliacoesDePerfil(String nomePerfil, Usuario logado) {

        if (nomePerfil == null || nomePerfil.isBlank()) {
            throw new IllegalArgumentException("O nome de perfil é inválido.");
        }

        boolean dono = logado != null && nomePerfil.equals(logado.getNomePerfil());
        List<Map<String, Object>> linhas = respostaDAO.respostasDoPerfil(nomePerfil, dono);

        Map<Long, Map<String, Object>> porFilme = new LinkedHashMap<>();
        for (Map<String, Object> linha : linhas) {
            Long avaliacaoId = ((Number) linha.get("avaliacaoId")).longValue();

            Map<String, Object> item = porFilme.get(avaliacaoId);
            if (item == null) {
                item = new HashMap<>();
                item.put("avaliacaoId", avaliacaoId);
                item.put("filmeId", linha.get("filmeId"));
                item.put("titulo", linha.get("titulo"));
                item.put("ano", linha.get("ano"));
                item.put("enviadaEm", linha.get("enviadaEm"));
                item.put("anonima", linha.get("anonima"));
                item.put("respostas", new ArrayList<Map<String, Object>>());
                porFilme.put(avaliacaoId, item);
            }

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("tema", linha.get("tema"));
            resposta.put("enunciado", linha.get("enunciado"));
            resposta.put("tipo", linha.get("tipo"));
            resposta.put("nota", linha.get("nota"));
            resposta.put("alternativaTexto", linha.get("alternativaTexto"));
            resposta.put("texto", linha.get("texto"));
            ((List<Map<String, Object>>) item.get("respostas")).add(resposta);
        }

        return new ArrayList<>(porFilme.values());
    }
}
