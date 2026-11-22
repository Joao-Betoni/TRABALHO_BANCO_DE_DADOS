package Trabalho_BD.model;
//Producao é a referencia que tanto diretor quanto ator para o filme.
//Funciona como uma FK para dizer producao_id(diretor)/producao_id(ator)

public class Producao {
    private Long id;
    private String nome;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }
    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
}
