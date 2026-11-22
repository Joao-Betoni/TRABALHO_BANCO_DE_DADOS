package Trabalho_BD.model;

public class Usuario {
    private Long id;
    private String nome;
    private String nomePerfil;
    private String email;
    private String senha;
    private boolean admin; // ESTOU COLOCANDO ADMIN AQUI MAS PODE TIRAR DEPOIS SE PRECISAR, APENAS VAI SER ÚTIL PRA DEMONSTRAR COISAS A MAIS

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
