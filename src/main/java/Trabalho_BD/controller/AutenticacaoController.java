package Trabalho_BD.controller;

import Trabalho_BD.model.Usuario;
import Trabalho_BD.dto.LoginDTO;
import Trabalho_BD.service.AutenticacaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService){
        this.autenticacaoService = autenticacaoService;
    }

    //Identificação de quem responde, login basico por sessão (RF5)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login, HttpSession session){
        Usuario usuario = autenticacaoService.autenticar(login.email(), login.senha());
        if(usuario == null){
            return ResponseEntity.status(401).body("Email ou senha invalidos");
        }
        // o Interceptor le "usuarioLogado" de TODAS as rotas /api
        session.setAttribute("usuarioLogado", usuario);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session){
        session.invalidate();
        return ResponseEntity.status(200).body("Sessao encerrada");
    }

    @GetMapping("/sessao")
    public ResponseEntity<?> sessao(HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        return usuario == null
                ? ResponseEntity.status(401).body("Nao logado")
                : ResponseEntity.ok(usuario);
    }
}
