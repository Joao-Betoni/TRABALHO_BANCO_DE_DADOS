package Trabalho_BD.model;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Usuario {

@NotBlank(message = "O e-mail é obrigatório")
@Email(message = "Informe um e-mail válido")
private String email;

String senha;


String nome;

Usuario(){};
Usuario(String email, String senha, String nome){
    this.email = email;
    this.senha = senha;
    this.nome = nome;
}

//Getters
String getEmail(){return email;}
String getSenha(){return senha;}
String getNome(){return nome;}

//Setters
void setEmail(String email){this.email=email;}
void setSenha(String senha){this.senha=senha;}
void setNome(String Nome){this.nome=nome;}
}
