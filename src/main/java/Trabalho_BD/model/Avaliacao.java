package Trabalho_BD.model;
//Avaliacao: o "formulario" de avaliacao montado para cada filme (1:1 com Filme)

import java.time.LocalDateTime;

public class Avaliacao {
    private Long id;
    private Long filmeId;     // fk -> Filme (UNIQUE: um formulario por filme)
    private String titulo;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getFilmeId(){
        return filmeId;
    }

    public void setFilmeId(Long filmeId){
        this.filmeId = filmeId;
    }

    public String getTitulo(){
        return titulo;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public String getDescricao(){
        return descricao;
    }

    public void setDescricao(String descricao){
        this.descricao = descricao;
    }

    public LocalDateTime getDataInicio(){
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio){
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim(){
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim){
        this.dataFim = dataFim;
    }
}
