package Trabalho_BD.model;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Usuario {


public enum categoria{
    ESPECTADOR,
    CINEFILO,
    CRITICO,

}


private Long id; //PK

@NotBlank(message = "O e-mail é obrigatório")
@Email(message = "Informe um e-mail válido")
private String email;

@NotBlank(message = "Senha não pode ser vazia")
private String senha;

@NotBlank(message = "O nome é obrigatório")
private String nome;

private String nomePerfil;

private categoria categoria;

private boolean admin;

Usuario(){};
Usuario(Long id,String email, String senha, String nome,String nomePerfil,categoria categoria, boolean admin){
    this.id = id;
    this.email = email;
    this.senha = senha;
    this.nome = nome;
    this.nomePerfil = nomePerfil;
    this.categoria = categoria;
    this.admin = admin;
}

//Getters

    public long getId(){return id;}

    public String getEmail(){return email;}
    public String getSenha(){return senha;}
    public String getNome(){return nome;}

    public String getNomePerfil() {return nomePerfil;}
    public categoria getCategoria() {return categoria;}
    public boolean isAdmin() {return admin;}

//Setters
    public void setId(long id){this.id = id;}

    public void setEmail(String email){this.email=email;}
    public void setSenha(String senha){this.senha=senha;}
    public void setNome(String Nome){this.nome=nome;}

    public void setNomePerfil(String nomePerfil) {this.nomePerfil = nomePerfil;}
    public void setCategoria(categoria categoria) {this.categoria = categoria;}
    public void setAdmin(boolean admin) {this.admin = admin;}
}
