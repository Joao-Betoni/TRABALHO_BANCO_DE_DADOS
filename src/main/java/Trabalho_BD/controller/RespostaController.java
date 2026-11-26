package Trabalho_BD.controller;

import Trabalho_BD.model.Usuario;
import Trabalho_BD.dto.FormularioRespostaDTO;
import Trabalho_BD.service.RespostaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uel/avaliacoes")
public class RespostaController {

    private final RespostaService respostaService;

    public RespostaController(RespostaService respostaService){
        this.respostaService = respostaService;
    }

    //Minhas avaliacoes: pendentes e feitas (nome temporario "me")
    @GetMapping("/me")
    public ResponseEntity<?> minhas(HttpSession session){
        Usuario usuario = SessaoAtual.usuario(session);
        if (usuario == null) {
            return ResponseEntity.status(401).body("Entre para ver suas avaliações.");
        }
        return ResponseEntity.ok(respostaService.minhasAvaliacoes(usuario.getId()));
    }

    //POST - O usuario avalia o filme. O service grava em 1 transacao:
    // INSERT avaliacao_usuario + batchUpdate das respostas (RF5)
    @PostMapping("/{id}/respostas")
    public ResponseEntity<?> responder(@PathVariable Long id,
                                       @RequestBody FormularioRespostaDTO form,
                                       HttpSession session){
        Usuario usuario = SessaoAtual.usuario(session);
        if (usuario == null) {
            return ResponseEntity.status(401).body("Entre para avaliar.");
        }
        try{
            respostaService.registrar(id, usuario.getId(), form);
            return ResponseEntity.status(201).build();
        } catch (IllegalArgumentException e){
            // questao obrigatoria vazia, nota fora da escala, filme ja avaliado
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // respostas detalhadas de um perfil (tela perfil.html). Sem sessao ou com
    // sessao de outra pessoa: somente as publicas; o dono ve as anonimas dele
    @GetMapping("/usuario/{nomePerfil}")
    public ResponseEntity<?> porPerfil(@PathVariable String nomePerfil,
                                       HttpSession session){
        Usuario usuario = SessaoAtual.usuario(session);
        return ResponseEntity.ok(respostaService.avaliacoesDePerfil(nomePerfil, usuario));
    }
}
