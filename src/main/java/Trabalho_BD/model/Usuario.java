package Trabalho_BD.model;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Usuario {

private Long id;

@NotBlank(message = "O e-mail é obrigatório")
@Email(message = "Informe um e-mail válido")
private String email;

@NotBlank(message = "Senha não pode ser vazia")
private String senha;

@NotBlank(message = "O nome é obrigatório")
private String nome;

Usuario(){};
Usuario(Long id,String email, String senha, String nome){
    this.id = id;
    this.email = email;
    this.senha = senha;
    this.nome = nome;
}

//Getters

public long getId(){return id;}
public String getEmail(){return email;}
public String getSenha(){return senha;}
public String getNome(){return nome;}

//Setters
public void setId(long id){this.id = id;}
public void setEmail(String email){this.email=email;}
public void setSenha(String senha){this.senha=senha;}
public void setNome(String Nome){this.nome=nome;}
}
