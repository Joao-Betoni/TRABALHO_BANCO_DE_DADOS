package Trabalho_BD.model;
//Direcao: qual Producao dirigiu qual Filme

public class FilmeDiretor {
    private Long filmeId;
    private Long producaoId;   // fk -> Producao

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
}
