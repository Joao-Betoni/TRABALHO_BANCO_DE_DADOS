package Trabalho_BD.controller;

import Trabalho_BD.service.PerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Perfil publico: nome, nome_perfil e a lista de filmes avaliados
// (as avaliacoes marcadas como anonimas NAO acusam o nome - RF6)
@RestController
@RequestMapping("/uel/perfil")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService){
        this.perfilService = perfilService;
    }

    @GetMapping("/{nomePerfil}")
    public ResponseEntity<?> perfil(@PathVariable String nomePerfil){
        var perfil = perfilService.buscarPorNomePerfil(nomePerfil);
        return perfil == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(perfil);
    }
}
