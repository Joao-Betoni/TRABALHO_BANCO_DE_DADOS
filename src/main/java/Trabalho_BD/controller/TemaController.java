package Trabalho_BD.controller;

import Trabalho_BD.model.Tema;
import Trabalho_BD.service.TemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// temas para agrupar as questoes na tela de responder (blocos por tema - RF6)
@RestController
@RequestMapping("/uel/temas")
public class TemaController {

    private final TemaService temaService;

    public TemaController(TemaService temaService){
        this.temaService = temaService;
    }

    @GetMapping
    public List<Tema> listar(){
        return temaService.listar();
    }
}
