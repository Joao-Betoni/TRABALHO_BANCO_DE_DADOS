package Trabalho_BD.dto;

import java.util.List;

// corpo do POST /api/avaliacoes/{id}/respostas (a nota do usuario ao filme)
public record FormularioRespostaDTO(
        boolean anonima,                      // publicar como anonimo (RF6)
        List<RespostaQuestaoDTO> respostas    // uma por questao do questionario
) {
}
