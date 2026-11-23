package Trabalho_BD.controller;

import Trabalho_BD.model.Questao;
import Trabalho_BD.dto.QuestaoFormDTO;
import Trabalho_BD.service.QuestaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// RF1: cadastro de questoes (ESCALA, ESCOLHA com alternativas, TEXTO)
// Usada no painel admin e na montagem do questionario do filme (RF3)
@RestController
@RequestMapping("/api/questoes")
public class QuestaoController {

    private final QuestaoService questaoService;

    public QuestaoController(QuestaoService questaoService){
        this.questaoService = questaoService;
    }

    @GetMapping
    public List<Questao> listar(){
        return questaoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Questao> buscar(@PathVariable Long id){
        Questao questao = questaoService.porId(id);
        return questao == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(questao);
    }

    // a transacao (questao + alternativas) vive no service
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody QuestaoFormDTO form, HttpSession session){
        try{
            Questao questao = questaoService.salvar(form);
            return ResponseEntity.status(201).body(questao);
        } catch (IllegalArgumentException e){
            // ex.: ESCOLHA precisa de 2 a 5 alternativas e exatamente 1 correta
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id, HttpSession session){
        try{
            questaoService.excluir(id);
            return ResponseEntity.status(200).build();
        } catch (IllegalArgumentException e){
            // questao usada em avaliacao nao pode ser excluida
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}
