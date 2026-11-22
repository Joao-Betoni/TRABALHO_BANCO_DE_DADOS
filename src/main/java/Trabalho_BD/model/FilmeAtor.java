package Trabalho_BD.model;
//Elenco: qual Producao atuou em qual Filme, e qual personagem

public class FilmeAtor {
    private Long filmeId;
    private Long producaoId;   // fk -> Producao
    private String personagem;

    public Long getFilmeId(){
        return filmeId;
    }

    public void setFilmeId(Long filmeId){
        this.filmeId = filmeId;
    }

    public Long getProducaoId(){
        return producaoId;
    }

    public void setProducaoId(Long producaoId){
        this.producaoId = producaoId;
    }

    public String getPersonagem(){
        return personagem;
    }

    public void setPersonagem(String personagem){
        this.personagem = personagem;
    }
}
