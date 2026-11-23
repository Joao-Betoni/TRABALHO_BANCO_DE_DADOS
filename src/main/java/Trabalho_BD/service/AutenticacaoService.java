
package Trabalho_BD.service;

import Trabalho_BD.model.Usuario;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService {

    private final UsuarioService usuarioService;

    public AutenticacaoService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario autenticar(String email, String senha) {

        if (email == null || email.isBlank() ||
                senha == null || senha.isBlank()) {
            return null;
        }

        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);

        if (usuario.isEmpty()) {
            return null;
        }

        if (!usuarioService.autenticarUsuario(senha, email)) {
            return null;
        }

        return usuario.get();
    }
}
