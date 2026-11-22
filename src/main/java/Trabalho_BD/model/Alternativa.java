package Trabalho_BD.model;
//Alternativa: as opcoes de uma questao do tipo ESCOLHA ("Onde voce assistiu?" -> Netflix, Cinema...)

public class Alternativa {
    private Long id;
    private Long questaoId;   // fk -> Questao
    private String texto;
    private boolean correta;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getQuestaoId(){
        return questaoId;
    }

    public void setQuestaoId(Long questaoId){
        this.questaoId = questaoId;
    }

    public String getTexto(){
        return texto;
    }

    public void setTexto(String texto){
        this.texto = texto;
    }

    public boolean isCorreta(){
        return correta;
    }

    public void setCorreta(boolean correta){
        this.correta = correta;
    }
}
