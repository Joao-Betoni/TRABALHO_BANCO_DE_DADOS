package Trabalho_BD.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class Avaliacao {

    private Long id;

    @NotNull
    private Long idFilme; //PK

    @NotNull
    private Long idUsuario; //FK

    private String tipo;
    private String titulo;
    private String descricao;

    private int dataInicio;
    private int dataFim;

Avaliacao(){}
Avaliacao(Long id,Long idUsuario,String tipo,String titulo, String descricao, int dataInicio, int dataFim){
    this.id = id;
    this.idUsuario = idUsuario;
    this.tipo = tipo;
    this.titulo = titulo;
    this.descricao = descricao;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
}

//getters

    //ids
    public Long getId() {return id;}
    public Long getIdUsuario() {return idUsuario;}
    public Long getIdFilme() {return idFilme;}

    public String gettipo() {return tipo;}
    public String gettitulo() {return titulo;}
    public String getdescricao() {return descricao;}

    public int getDataInicio() {return dataInicio;}
    public int getDataFim() {return dataFim;}


//setters
    //ids
    public void setId(Long id) {this.id = id;}
    public void setIdUsuario(Long idUsuario) {this.idUsuario = idUsuario;}
    public void setIdFilme(Long idFilme) {this.idFilme = idFilme;}

    public void settipo(String tipo) {tipo = tipo;}
    public void settitulo(String titulo) {titulo = titulo;}
    public void setdescricao(String descricao) {descricao = descricao;}

    public void setDataInicio(int dataInicio) {this.dataInicio = dataInicio;}
    public void setDataFim(int dataFim) {this.dataFim = dataFim;}
}
