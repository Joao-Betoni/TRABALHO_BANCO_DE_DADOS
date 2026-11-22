package Trabalho_BD.model;
//AvaliacaoQuestao: quais questoes compoem a avaliacao de um filme e em qual ordem (RF3)
//A mesma questao pode compor avaliacoes de filmes diferentes

public class AvaliacaoQuestao {
    private Long avaliacaoId;
    private Long questaoId;
    private int ordem;

    public Long getAvaliacaoId(){
        return avaliacaoId;
    }

    public void setAvaliacaoId(Long avaliacaoId){
        this.avaliacaoId = avaliacaoId;
    }

    public Long getQuestaoId(){
        return questaoId;
    }

    public void setQuestaoId(Long questaoId){
        this.questaoId = questaoId;
    }

    public int getOrdem(){
        return ordem;
    }

    public void setOrdem(int ordem){
        this.ordem = ordem;
    }
}
