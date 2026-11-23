package Trabalho_BD.model;

import java.time.LocalDateTime; //este import serve para atualizar o cadastro exato de quando foi feito o cadastro.

//Filmes do catalogo
public class Filme {
    private Long id;
    private String titulo;
    private int ano;
    private Long generoId; //fk de Genero
    private Integer duracaoMin; //não posso esquecer de colocar que aceita null
    private String sinopse;
    private Long cadastradoPor;
    private LocalDateTime cadastradoEm;

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getTitulo(){
        return titulo;
    }
    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public int getAno(){
        return ano;
    }
    public void setAno(int ano){
        this.ano = ano;
    }

    public Long getGeneroId(){
        return generoId;
    }
    public void setGeneroId(Long generoId){
        this.generoId = generoId;
    }

    public Integer getDuracaoMin() {
        return duracaoMin;
    }

    public void setDuracaoMin(Integer duracaoMin) {
        this.duracaoMin = duracaoMin;
    }

    public String getSinopse() {
        return sinopse;
    }
    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }
//Os cadastros de filme o próprio usuário pode realizar! (ou o administrador)
    public Long getCadastradoPor() {
        return cadastradoPor;
    }

    public void setCadastradoPor(Long cadastradoPor) {
        this.cadastradoPor = cadastradoPor;
    }
    public LocalDateTime getCadastradoEm(){
        return cadastradoEm;
    }
    public void setCadastradoEm(LocalDateTime cadastradoEm){
        this.cadastradoEm = cadastradoEm;
    }
}
