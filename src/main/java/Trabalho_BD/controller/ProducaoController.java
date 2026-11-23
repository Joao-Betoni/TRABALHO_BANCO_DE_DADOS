package Trabalho_BD.controller;

import Trabalho_BD.model.Producao;
import Trabalho_BD.service.ProducaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// autocomplete de atores/diretores no formulario de filme (?busca=fern)
@RestController
@RequestMapping("/api/producoes")
public class ProducaoController {

    private final ProducaoService producaoService;

    public ProducaoController(ProducaoService producaoService){
        this.producaoService = producaoService;
    }

    @GetMapping
    public List<Producao> buscar(@RequestParam(required = false) String busca){
        return producaoService.buscar(busca);
    }
}
