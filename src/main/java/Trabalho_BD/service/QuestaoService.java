
package Trabalho_BD.service;

import Trabalho_BD.dao.QuestaoDAO;
import Trabalho_BD.dao.AlternativaDAO;
import Trabalho_BD.dto.QuestaoFormDTO;
import Trabalho_BD.model.Questao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class QuestaoService {

    private final QuestaoDAO questaoDAO;
    private final AlternativaDAO alternativaDAO;

    public QuestaoService(QuestaoDAO questaoDAO,
                          AlternativaDAO alternativaDAO) {
        this.questaoDAO = questaoDAO;
        this.alternativaDAO = alternativaDAO;
    }

    private void validarDadosQuestao(QuestaoFormDTO form) {

        if (form == null) {
            throw new IllegalArgumentException(
                    "Dados da questão não informados."
            );
        }

        if (form.enunciado() == null || form.enunciado().isBlank()) {
            throw new IllegalArgumentException(
                    "O enunciado da questão é obrigatório."
            );
        }

        if (form.tipo() == null || form.tipo().isBlank()) {
            throw new IllegalArgumentException(
                    "O tipo da questão é obrigatório."
            );
        }

        if (!form.tipo().equals("ESCALA") &&
                !form.tipo().equals("ESCOLHA") &&
                !form.tipo().equals("TEXTO")) {
            throw new IllegalArgumentException(
                    "O tipo deve ser ESCALA, ESCOLHA ou TEXTO."
            );
        }

        if (form.temaId() != null && form.temaId() <= 0) {
            throw new IllegalArgumentException(
                    "O ID do tema é inválido."
            );
        }

        if (form.tipo().equals("ESCALA")) {

            int escalaMin = form.escalaMin() == null
                    ? 1 : form.escalaMin();

            int escalaMax = form.escalaMax() == null
                    ? 5 : form.escalaMax();

            if (escalaMin >= escalaMax) {
                throw new IllegalArgumentException(
                        "O valor mínimo da escala deve ser menor que o máximo."
                );
            }

            if (escalaMin < Short.MIN_VALUE ||
                    escalaMax > Short.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Os limites da escala excedem o permitido."
                );
            }
        }

        if (form.tipo().equals("ESCOLHA")) {

            if (form.alternativas() == null ||
                    form.alternativas().size() < 2 ||
                    form.alternativas().size() > 5) {
                throw new IllegalArgumentException(
                        "Questões de escolha devem possuir de 2 a 5 alternativas."
                );
            }

            long quantidadeCorretas = 0;
            Set<String> textos = new HashSet<>();

            for (QuestaoFormDTO.AlternativaDTO alternativa :
                    form.alternativas()) {

                if (alternativa == null ||
                        alternativa.texto() == null ||
                        alternativa.texto().isBlank()) {
                    throw new IllegalArgumentException(
                            "O texto das alternativas é obrigatório."
                    );
                }

                if (alternativa.texto().length() > 255) {
                    throw new IllegalArgumentException(
                            "Cada alternativa deve ter no máximo 255 caracteres."
                    );
                }

                if (!textos.add(alternativa.texto())) {
                    throw new IllegalArgumentException(
                            "Não é permitido repetir alternativas."
                    );
                }

                if (alternativa.correta()) {
                    quantidadeCorretas++;
                }
            }

            if (quantidadeCorretas != 1) {
                throw new IllegalArgumentException(
                        "A questão deve possuir exatamente uma alternativa correta."
                );
            }

        } else if (form.alternativas() != null &&
                !form.alternativas().isEmpty()) {
            throw new IllegalArgumentException(
                    "Somente questões de escolha podem possuir alternativas."
            );
        }
    }

    @Transactional
    public Questao salvar(QuestaoFormDTO form) {

        validarDadosQuestao(form);

        Questao questao = new Questao();

        questao.setEnunciado(form.enunciado());
        questao.setTipo(form.tipo());
        questao.setTemaId(form.temaId());

        if (form.tipo().equals("ESCALA")) {
            questao.setEscalaMin(
                    form.escalaMin() == null ? 1 : form.escalaMin()
            );

            questao.setEscalaMax(
                    form.escalaMax() == null ? 5 : form.escalaMax()
            );
        } else {
            questao.setEscalaMin(1);
            questao.setEscalaMax(5);
        }

        questao.setObrigatoria(
                form.obrigatoria() == null || form.obrigatoria()
        );

        Long id = questaoDAO.inserirQuestao(questao);

        if (form.tipo().equals("ESCOLHA")) {
            for (QuestaoFormDTO.AlternativaDTO alternativa :
                    form.alternativas()) {

                alternativaDAO.inserirAlternativa(id, alternativa);
            }
        }

        return questaoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalStateException(
                        "A questão foi cadastrada, mas não foi encontrada."
                ));
    }

    public Optional<Questao> buscarPorId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID da questão é inválido."
            );
        }

        return questaoDAO.buscarPorId(id);
    }

    public Questao porId(Long id) {
        return buscarPorId(id).orElse(null);
    }

    public List<Questao> listar() {
        return questaoDAO.listarQuestoes();
    }

    public List<Questao> listarPorTipo(String tipo) {

        if (tipo == null ||
                (!tipo.equals("ESCALA") &&
                        !tipo.equals("ESCOLHA") &&
                        !tipo.equals("TEXTO"))) {
            throw new IllegalArgumentException(
                    "O tipo da questão é inválido."
            );
        }

        return questaoDAO.listarPorTipo(tipo);
    }

    public List<Questao> listarPorTema(Long temaId) {

        if (temaId == null || temaId <= 0) {
            throw new IllegalArgumentException(
                    "O ID do tema é inválido."
            );
        }

        return questaoDAO.listarPorTema(temaId);
    }

    @Transactional
    public void excluir(Long id) {

        if (buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "Questão não encontrada."
            );
        }

        try {
            questaoDAO.excluirQuestao(id);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    "A questão não pode ser excluída porque possui registros vinculados.",
                    e
            );
        }
    }
}
