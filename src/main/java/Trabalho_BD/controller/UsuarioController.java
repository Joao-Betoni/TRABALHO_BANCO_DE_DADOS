package Trabalho_BD.controller;

import Trabalho_BD.model.Usuario;
import Trabalho_BD.dto.UsuarioFormDTO;
import Trabalho_BD.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Cadastro de usuario (a tela cadastre-se) e listagem de perfis
@RestController
@RequestMapping("/uel/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    // criar conta (login simples: email + senha)
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody UsuarioFormDTO form){
        try{
            Usuario usuario = usuarioService.salvar(form);
            return ResponseEntity.status(201).body(usuario);
        } catch (IllegalArgumentException e){
            // email ou nome_perfil ja existem
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id){
        Usuario usuario = usuarioService.porId(id);
        return usuario == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(usuario);
    }
}
