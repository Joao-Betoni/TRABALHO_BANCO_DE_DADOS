package Trabalho_BD.controller;

import Trabalho_BD.model.Usuario;
import jakarta.servlet.http.HttpSession;

// leitura da sessao pelos controllers: devolve o usuario logado ou null.
// Sem interceptor, cada rota que precisa de login guarda a si mesma:
//   Usuario usuario = SessaoAtual.usuario(session);
//   if (usuario == null) return ResponseEntity.status(401).body(...);
public final class SessaoAtual {

    private SessaoAtual() {}

    public static Usuario usuario(HttpSession session) {
        return session == null
                ? null
                : (Usuario) session.getAttribute("usuarioLogado");
    }
}
