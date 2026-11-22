package Trabalho_BD.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Diretor {

    private Long drt; //PK

    @NotNull
    private Long idFilme;//FK

    @NotBlank
    private String nome;

Diretor(){}
Diretor(Long drt, Long idFilme, String nome){
    this.drt = drt;
    this.idFilme = idFilme;
    this.nome = nome;
}

//getters
    public Long getDrt() {return drt;}
    public Long getIdFilme() {return idFilme;}
    public String getNome() {return nome;}
//setters
    public void setDrt(Long drt) {this.drt = drt;}
    public void setIdFilme(Long idFilme) {this.idFilme = idFilme;}
    public void setNome(String nome) {this.nome = nome;}
}
