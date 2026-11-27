package Trabalho_BD.dto;

import java.util.List;

// corpo do POST /api/questoes (RF1)
// ESCALA: nota/alternativaId ignoradas; ESCOLHA: exige alternativas; TEXTO: escala ignorada
public record QuestaoFormDTO(
        String enunciado,
        String tipo,               // ESCALA | ESCOLHA | TEXTO
        Long temaId,
        Integer escalaMin,         // so para ESCALA (default 1)
        Integer escalaMax,         // so para ESCALA (default 5)
        Boolean obrigatoria,
        List<AlternativaDTO> alternativas
) {
    public record AlternativaDTO(String texto, boolean correta) {
    }
}
