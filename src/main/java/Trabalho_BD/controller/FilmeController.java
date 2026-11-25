package Trabalho_BD.controller;

import Trabalho_BD.model.Filme;
import Trabalho_BD.model.Usuario;
import Trabalho_BD.dto.FilmeFormDTO;
import Trabalho_BD.service.FilmeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uel/filmes")
public class FilmeController {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService){
        this.filmeService = filmeService;
    }

    //GET - Catalogo com filtros de tela (?genero=2&busca=deus)
    @GetMapping
    public List<Filme> listar(@RequestParam(required = false) Long genero,
                              @RequestParam(required = false) String busca){
        return filmeService.listar(genero, busca);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Filme> buscar(@PathVariable Long id){
        Filme filme = filmeService.porId(id);
        return filme == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(filme);
    }

    //POST - Cadastro de filme. A transacao multi-tabela (filme + avaliacao
    // + avaliacao_questao + destinatarios) vive DENTRO do service (RF2/RF3/RF4)
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody FilmeFormDTO form, HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        try{
            Filme filme = filmeService.cadastrar(form, usuario.getId());
            return ResponseEntity.status(201).body(filme);
        } catch (IllegalArgumentException e){
            // ex.: UNIQUE(titulo, ano) -> "Esse filme ja esta no catalogo"
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    //GET - Questionario do filme (a tela responder.html renderiza por tema)
    @GetMapping("/{id}/questionario")
    public ResponseEntity<?> questionario(@PathVariable Long id){
        var questionario = filmeService.questionarioDe(id);
        return questionario == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(questionario);
    }
}
