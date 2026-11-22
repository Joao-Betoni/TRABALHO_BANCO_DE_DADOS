package Trabalho_BD.model;
//Questao: a pergunta em si, cadastrada uma vez e reaproveitada em varios filmes (RF1, RF3)
//Tipos: ESCALA (nota 1 a 5), ESCOLHA (multipla escolha com alternativas), TEXTO (livre)

public class Questao {
    private Long id;
    private String enunciado;
    private String tipo;
    private Long temaId;        // fk -> Tema
    private Integer escalaMin;  // so para ESCALA (default 1)
    private Integer escalaMax;  // so para ESCALA (default 5)
    private boolean obrigatoria;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getEnunciado(){
        return enunciado;
    }

    public void setEnunciado(String enunciado){
        this.enunciado = enunciado;
    }

    public String getTipo(){
        return tipo;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public Long getTemaId(){
        return temaId;
    }

    public void setTemaId(Long temaId){
        this.temaId = temaId;
    }

    public Integer getEscalaMin(){
        return escalaMin;
    }

    public void setEscalaMin(Integer escalaMin){
        this.escalaMin = escalaMin;
    }

    public Integer getEscalaMax(){
        return escalaMax;
    }

    public void setEscalaMax(Integer escalaMax){
        this.escalaMax = escalaMax;
    }

    public boolean isObrigatoria(){
        return obrigatoria;
    }

    public void setObrigatoria(boolean obrigatoria){
        this.obrigatoria = obrigatoria;
    }
}
