package Trabalho_BD.model;
//AvaliacaoUsuario: a avaliacao que um usuario fez de um filme (ex-Tentativa)
//UNIQUE(avaliacaoId, usuarioId): cada usuario avalia cada filme uma unica vez
//anonima: publicar como anonimo (o nome nao aparece nos relatorios/perfis) - RF6

import java.time.LocalDateTime;

public class AvaliacaoUsuario {
    private Long id;
    private Long avaliacaoId;   // fk -> Avaliacao
    private Long usuarioId;     // fk -> Usuario
    private LocalDateTime enviadaEm;
    private boolean anonima;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getAvaliacaoId(){
        return avaliacaoId;
    }

    public void setAvaliacaoId(Long avaliacaoId){
        this.avaliacaoId = avaliacaoId;
    }

    public Long getUsuarioId(){
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId){
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getEnviadaEm(){
        return enviadaEm;
    }

    public void setEnviadaEm(LocalDateTime enviadaEm){
        this.enviadaEm = enviadaEm;
    }

    public boolean isAnonima(){
        return anonima;
    }

    public void setAnonima(boolean anonima){
        this.anonima = anonima;
    }
}
