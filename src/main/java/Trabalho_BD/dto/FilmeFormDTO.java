package Trabalho_BD.dto;

import java.util.List;

// corpo do POST /api/filmes (cadastro de filme no catalogo)
public record FilmeFormDTO(
        String titulo,
        int ano,
        Long generoId,
        Integer duracaoMin,
        String sinopse,
        List<String> diretores,
        List<String> atores
) {
}
