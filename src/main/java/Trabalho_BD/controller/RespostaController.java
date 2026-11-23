package Trabalho_BD.controller;

import Trabalho_BD.model.Usuario;
import Trabalho_BD.dto.FormularioRespostaDTO;
import Trabalho_BD.service.RespostaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avaliacoes")
public class RespostaController {

    private final RespostaService respostaService;

    public RespostaController(RespostaService respostaService){
        this.respostaService = respostaService;
    }

    //Minhas avaliacoes: pendentes e feitas (nome temporario "me")
    @GetMapping("/me")
    public ResponseEntity<?> minhas(HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        return ResponseEntity.ok(respostaService.minhasAvaliacoes(usuario.getId()));
    }

    //POST - O usuario avalia o filme. O service grava em 1 transacao:
    // INSERT avaliacao_usuario + batchUpdate das respostas (RF5)
    @PostMapping("/{id}/respostas")
    public ResponseEntity<?> responder(@PathVariable Long id,
                                       @RequestBody FormularioRespostaDTO form,
                                       HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        try{
            respostaService.registrar(id, usuario.getId(), form);
            return ResponseEntity.status(201).build();
        } catch (IllegalArgumentException e){
            // questao obrigatoria vazia, nota fora da escala, filme ja avaliado
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
