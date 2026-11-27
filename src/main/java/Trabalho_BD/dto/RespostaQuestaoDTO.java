package Trabalho_BD.dto;

// resposta a UMA questao: exatamente um dos tres campos preenchido
// nota (ESCALA 1-5) ou alternativaId (ESCOLHA) ou texto (TEXTO/critica)
public record RespostaQuestaoDTO(
        Long questaoId,
        Integer nota,
        Long alternativaId,
        String texto
) {
}
