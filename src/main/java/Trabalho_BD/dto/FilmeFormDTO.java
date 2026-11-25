package Trabalho_BD.dto;

// corpo do POST /api/filmes (cadastro de filme no catalogo) - sem elenco;
// o cadastro ja cria automaticamente o questionario de avaliacao (RF4)
public record FilmeFormDTO(
        String titulo,
        int ano,
        Long generoId,
        Integer duracaoMin,
        String sinopse
) {
}
