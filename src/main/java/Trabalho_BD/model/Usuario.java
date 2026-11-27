package Trabalho_BD.model;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {
    private Long id;
    private String nome;
    private String nomePerfil;
    private String email;
    private String senha;
    private boolean admin; // ESTOU COLOCANDO ADMIN AQUI MAS PODE TIRAR DEPOIS SE PRECISAR, APENAS VAI SER ÚTIL PRA DEMONSTRAR COISAS A MAIS

public Usuario(){}
public Usuario(Long id, String nome, String nomePerfil, String email, String senha, boolean admin){
    this.id = id;
    this.nome = nome;
    this.nomePerfil = nomePerfil;
    this.email = email;
    this.senha = senha;
    this.admin = admin;
}

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

    public String getNomePerfil() {
        return nomePerfil;
    }

    public void setNomePerfil(String nomePerfil) {
        this.nomePerfil = nomePerfil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
