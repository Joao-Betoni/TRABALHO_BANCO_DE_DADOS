package Trabalho_BD.controller;

import Trabalho_BD.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// RF7: os 5 relatorios, cada um = tabela + grafico no front (Chart.js)
// O SQL de cada um vive no RelatorioDAO, espelhando o database/relatorios.sql
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService){
        this.relatorioService = relatorioService;
    }

    // R1 - media por tema x genero de filme (JOIN, GROUP BY/HAVING, IN)
    @GetMapping("/temas-genero")
    public ResponseEntity<?> temasPorGenero(@RequestParam(required = false) String generos){
        return ResponseEntity.ok(relatorioService.temasPorGenero(generos));
    }

    // R2 - top 10 filmes por estrelas (subconsulta, RANK(), FETCH FIRST, ILIKE)
    @GetMapping("/ranking")
    public ResponseEntity<?> top10(@RequestParam(required = false) String busca){
        return ResponseEntity.ok(relatorioService.top10(busca));
    }

    // R3 - filmes sem avaliacao / questoes nunca usadas (LEFT/RIGHT JOIN)
    @GetMapping("/sem-avaliacao")
    public ResponseEntity<?> semAvaliacao(){
        return ResponseEntity.ok(relatorioService.semAvaliacao());
    }

    // R4 - evolucao semanal das notas por grupo (date_trunc, datas, IN)
    @GetMapping("/evolucao")
    public ResponseEntity<?> evolucao(@RequestParam(required = false) String categorias,
                                      @RequestParam(required = false) String inicio,
                                      @RequestParam(required = false) String fim){
        return ResponseEntity.ok(relatorioService.evolucao(categorias, inicio, fim));
    }

    // R5 - temas mais criticados, % de notas 1-2 (BETWEEN, RANK(), HAVING)
    @GetMapping("/criticados")
    public ResponseEntity<?> maisCriticados(){
        return ResponseEntity.ok(relatorioService.maisCriticados());
    }
}
