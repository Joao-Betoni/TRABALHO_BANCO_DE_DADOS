package Trabalho_BD.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Ator {


    private Long drt; //PK

    @NotNull
    private Long idFilme; //FK

    @NotBlank(message = "Nome do ator não pode ser vazio")
    private String nome;

    @NotBlank(message = "O nome do personagem não pode ser Nulo")
    private String personagem;


Ator(){};
Ator(Long drt, Long idFilme,String nome, String personagem){
    this.drt = drt;
    this.idFilme = idFilme;
    this.nome = nome;
    this.personagem = personagem;
}

//getters

    public Long getDrt() {return drt;}
    public Long getIdFilme() {return idFilme;}
    public String getNome() {return nome;}
    public String getPersonagem() {return personagem;}

//setters
    public void setDrt(Long drt) {this.drt = drt;}
    public void setIdFilme(Long idFilme) {this.idFilme = idFilme;}
    public void setNome(String nome) {this.nome = nome;}
    public void setPersonagem(String personagem) {this.personagem = personagem;}
}
