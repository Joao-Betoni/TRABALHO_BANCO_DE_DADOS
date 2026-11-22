package Trabalho_BD.model;
//Tema: categoria da questao. As questoes em escala sao agrupadas por tema (RF6)
//Temas de carga: Avaliacao Geral, Critica, Plataforma, Roteiro, Direcao, Atuacao, Trilha Sonora

public class Tema {
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
