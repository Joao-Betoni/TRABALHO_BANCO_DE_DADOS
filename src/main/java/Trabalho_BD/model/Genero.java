package Trabalho_BD.model;

public class Genero {

    private Long id;

    private String nome;

Genero(){}
Genero(Long id,String nome){
    this.id =id;
    this.nome=nome;
}
//getters
    public Long getId(){return id;}
    public String nome(){return nome;}
//setters
    public void setId(Long id) {this.id = id;}
    public String getNome() {return nome;}
}
