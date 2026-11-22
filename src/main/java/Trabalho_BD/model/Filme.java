package Trabalho_BD.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class Filme {


    private Long id;

    @NotNull
    private Long idUsuarioCadastro;

    @NotBlank (message ="Título não pode ser vazio")
    private String titulo;

    @Min(value =1, message = "A duração não pode ser zero")
    private int duracao; //EM MINUTOS

    private int ano;

Filme(){}
Filme(Long id,Long idUsuarioCadastro,String titulo,int duracao,int ano){
    this.id =id;
    this.idUsuarioCadastro=idUsuarioCadastro;
    this.titulo=titulo;
    this.duracao=duracao;
    this.ano=ano;
}

//getters
    public Long getId() {return id;}
    public Long getIdUsuarioCadastro() {return idUsuarioCadastro;}
    public String getTitulo(){return titulo;}
    public int getDuracao(){return duracao;}
    public int getAno(){return ano;}

//setters
    public void setId(Long id) {this.id = id;}
    public void setIdUsuarioCadastro(Long idUsuarioCadastro){this.idUsuarioCadastro = idUsuarioCadastro;}
    public void setTitulo(String titulo){this.titulo=titulo;}
    public void setDuracao(int duracao) {this.duracao = duracao;}
    public void setAno(int ano) {this.ano = ano;}
}
