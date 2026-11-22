package Trabalho_BD.model;
//AvaliacaoDestinatario: quais usuarios devem (podem) responder a avaliacao do filme (RF4)
//Ao cadastrar um filme, o sistema atribui a todos os usuarios automaticamente

import java.time.LocalDateTime;

public class AvaliacaoDestinatario {
    private Long avaliacaoId;
    private Long usuarioId;
    private LocalDateTime atribuidaEm;

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

    public LocalDateTime getAtribuidaEm(){
        return atribuidaEm;
    }

    public void setAtribuidaEm(LocalDateTime atribuidaEm){
        this.atribuidaEm = atribuidaEm;
    }
}
