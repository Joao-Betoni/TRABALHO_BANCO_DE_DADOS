package Trabalho_BD.model;
//Resposta: a resposta dada a UMA questao dentro de UMA avaliacao de usuario (RF5)
//Deve ter exatamente UMA das tres: nota (ESCALA), alternativaId (ESCOLHA) ou texto (TEXTO)

public class Resposta {
    private Long id;
    private Long avaliacaoUsuarioId;   // fk -> AvaliacaoUsuario
    private Long questaoId;            // fk -> Questao
    private Long alternativaId;        // fk -> Alternativa (so para ESCOLHA)
    private Integer nota;              // so para ESCALA (1 a 5, as estrelas)
    private String texto;              // so para TEXTO (a critica)

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getAvaliacaoUsuarioId(){
        return avaliacaoUsuarioId;
    }

    public void setAvaliacaoUsuarioId(Long avaliacaoUsuarioId){
        this.avaliacaoUsuarioId = avaliacaoUsuarioId;
    }

    public Long getQuestaoId(){
        return questaoId;
    }

    public void setQuestaoId(Long questaoId){
        this.questaoId = questaoId;
    }

    public Long getAlternativaId(){
        return alternativaId;
    }

    public void setAlternativaId(Long alternativaId){
        this.alternativaId = alternativaId;
    }

    public Integer getNota(){
        return nota;
    }

    public void setNota(Integer nota){
        this.nota = nota;
    }

    public String getTexto(){
        return texto;
    }

    public void setTexto(String texto){
        this.texto = texto;
    }
}
