package Trabalho_BD.controller;

import Trabalho_BD.model.Genero;
import Trabalho_BD.service.GeneroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// generos para o filtro do catalogo e o select do cadastro de filme
@RestController
@RequestMapping("/uel/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService){
        this.generoService = generoService;
    }

    @GetMapping
    public List<Genero> listar(){
        return generoService.listar();
    }
}
