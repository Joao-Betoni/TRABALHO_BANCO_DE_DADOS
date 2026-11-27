package Trabalho_BD.dto;

// corpo do POST /api/usuarios (a tela cadastre-se)
public record UsuarioFormDTO(
        String nome,
        String nomePerfil,
        String email,
        String senha
) {
}
