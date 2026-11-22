package Trabalho_BD.model;

import jakarta.validation.constraints.NotNull;

public class Questoes {

    private Long id; //PK

    @NotNull
    private Long temaId; //FK

    private String enunciado;
    private String tipo;
    private String escala;

Questoes(){}
Questoes(Long id, Long temaId,String enunciado, String tipo, String escala){
    this.id = id;
    this.temaId = temaId;
    this.enunciado = enunciado;
    this.tipo = tipo;
    this.escala = escala;
}

//getters

    public Long getId() {return id;}
    public Long getTemaId() {return temaId;}
    public String getEnunciado() {return enunciado;}
    public String getEscala() {return escala;}
    public String getTipo() {return tipo;}

    //setters
    public void setId(Long id) {this.id = id;}
    public void setTemaId(Long temaId) {this.temaId = temaId;}
    public void setEnunciado(String enunciado) {this.enunciado = enunciado;}
    public void setEscala(String escala) {this.escala = escala;}
    public void setTipo(String tipo) {this.tipo = tipo;}

}
