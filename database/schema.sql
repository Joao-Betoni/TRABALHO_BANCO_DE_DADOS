-- =============================================================================
-- Cine Central do Brasil - schema do banco (PostgreSQL)
-- Rodar em um banco VAZIO. Ordem obrigatoria:
--   1) schema.sql   2) carga.sql   3) relatorios.sql
-- 11 tabelas: usuario, genero, filme, tema, questao, alternativa, avaliacao,
-- avaliacao_questao, avaliacao_destinatario, avaliacao_usuario, resposta
-- =============================================================================

-- Usuario: quem usa a plataforma. categoria separa os grupos de respondentes
-- usados nos relatorios (RF6). admin libera cadastros de questao/tema/genero.
CREATE TABLE usuario(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    nome_perfil VARCHAR(60)  NOT NULL,
    email       VARCHAR(160) NOT NULL,
    senha       VARCHAR(80)  NOT NULL,
    categoria   VARCHAR(20)  NOT NULL DEFAULT 'ESPECTADOR'
                CHECK (categoria IN ('ESPECTADOR','CINEFILO','CRITICO')),
    admin       BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_usuario_email UNIQUE (email),
    CONSTRAINT uq_usuario_perfil UNIQUE (nome_perfil)
);

CREATE TABLE genero(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(40) NOT NULL,
    CONSTRAINT uq_genero_nome UNIQUE (nome)
);

-- Filme: o catalogo. UNIQUE(titulo, ano) nao deixa cadastrar o mesmo filme 2x.
CREATE TABLE filme(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo         VARCHAR(150) NOT NULL,
    ano            SMALLINT     NOT NULL,
    genero_id      BIGINT       NOT NULL,
    duracao_min    SMALLINT     NULL,
    sinopse        TEXT         NULL,
    cadastrado_por BIGINT       NOT NULL,
    cadastrado_em  TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_filme_genero  FOREIGN KEY (genero_id)      REFERENCES genero(id)  ON DELETE RESTRICT,
    CONSTRAINT fk_filme_usuario FOREIGN KEY (cadastrado_por) REFERENCES usuario(id) ON DELETE RESTRICT,
    CONSTRAINT chk_filme_ano     CHECK (ano BETWEEN 1895 AND 2100),
    CONSTRAINT chk_filme_duracao CHECK (duracao_min IS NULL OR duracao_min > 0),
    CONSTRAINT uq_filme_titulo_ano UNIQUE (titulo, ano)
);

-- Tema: agrupa as questoes em blocos na tela de responder e e a dimensao dos
-- relatorios ("Avaliacao Geral", "Roteiro", "Direcao", "Atuacao"...)
CREATE TABLE tema(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(60) NOT NULL,
    CONSTRAINT uq_tema_nome UNIQUE (nome)
);

-- Questao: ESCALA = nota de 1 a 5 (estrelas), ESCOLHA = uma alternativa,
-- TEXTO = critica escrita. Reaproveitada entre filmes via avaliacao_questao (RF3).
CREATE TABLE questao(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    enunciado   TEXT        NOT NULL,
    tipo        VARCHAR(20) NOT NULL,
    tema_id     BIGINT      NOT NULL,
    escala_min  SMALLINT    NOT NULL DEFAULT 1,
    escala_max  SMALLINT    NOT NULL DEFAULT 5,
    obrigatoria BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_questao_tema FOREIGN KEY (tema_id) REFERENCES tema(id) ON DELETE RESTRICT,
    CONSTRAINT chk_questao_tipo   CHECK (tipo IN ('ESCALA','ESCOLHA','TEXTO')),
    CONSTRAINT chk_questao_escala CHECK (escala_min < escala_max)
);

CREATE TABLE alternativa(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    questao_id BIGINT       NOT NULL,
    texto      VARCHAR(255) NOT NULL,
    correta    BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_alternativa_questao FOREIGN KEY (questao_id) REFERENCES questao(id) ON DELETE CASCADE,
    CONSTRAINT uq_alternativa UNIQUE (questao_id, texto)
);

-- Avaliacao: o instrumento de avaliacao - 1 para cada filme (RF1: avaliacao
-- sempre vinculada a um unico filme).
CREATE TABLE avaliacao(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    filme_id    BIGINT      NOT NULL,
    titulo      VARCHAR(150) NOT NULL,
    descricao   TEXT        NOT NULL,
    data_inicio TIMESTAMP   NOT NULL DEFAULT now(),
    data_fim    TIMESTAMP   NULL,
    CONSTRAINT fk_avaliacao_filme FOREIGN KEY (filme_id) REFERENCES filme(id) ON DELETE CASCADE,
    CONSTRAINT uq_avaliacao_filme UNIQUE (filme_id),
    CONSTRAINT chk_avaliacao_periodo CHECK (data_fim IS NULL OR data_fim > data_inicio)
);

-- Quais questoes compoem o instrumento e em que ordem aparecem na tela
CREATE TABLE avaliacao_questao(
    avaliacao_id BIGINT   NOT NULL,
    questao_id   BIGINT   NOT NULL,
    ordem        SMALLINT NOT NULL,
    CONSTRAINT pk_avaliacao_questao PRIMARY KEY (avaliacao_id, questao_id),
    CONSTRAINT fk_aq_avaliacao FOREIGN KEY (avaliacao_id) REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_aq_questao   FOREIGN KEY (questao_id)   REFERENCES questao(id)   ON DELETE RESTRICT,
    CONSTRAINT uq_aq_ordem UNIQUE (avaliacao_id, ordem)
);

-- A quem o instrumento foi direcionado (RF4: na hora de cadastrar o filme
-- vai para todos os usuarios, tudo na mesma transacao)
CREATE TABLE avaliacao_destinatario(
    avaliacao_id BIGINT NOT NULL,
    usuario_id   BIGINT NOT NULL,
    atribuida_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_avaliacao_destinatario PRIMARY KEY (avaliacao_id, usuario_id),
    CONSTRAINT fk_ad_avaliacao FOREIGN KEY (avaliacao_id) REFERENCES avaliacao(id)     ON DELETE CASCADE,
    CONSTRAINT fk_ad_usuario   FOREIGN KEY (usuario_id)   REFERENCES usuario(id)     ON DELETE CASCADE
);

-- Avaliacao_usuario (a "tentativa"): 1 resposta de 1 usuario a 1 instrumento.
-- anonima = escolha do usuario ao publicar (RF6); relatorios e perfil publico
-- nao expoem o autor quando anonima = TRUE.
CREATE TABLE avaliacao_usuario(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    avaliacao_id BIGINT    NOT NULL,
    usuario_id   BIGINT    NOT NULL,
    enviada_em   TIMESTAMP NOT NULL DEFAULT now(),
    anonima      BOOLEAN   NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_au_avaliacao FOREIGN KEY (avaliacao_id) REFERENCES avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_au_usuario   FOREIGN KEY (usuario_id)   REFERENCES usuario(id)   ON DELETE CASCADE,
    CONSTRAINT uq_au_unica UNIQUE (avaliacao_id, usuario_id)    -- 1 resposta por usuario
);

-- Resposta: cada resposta vale para 1 questao de 1 instrumento respondido.
-- XOR: nota (ESCALA) OU alternativa_id (ESCOLHA) OU texto (TEXTO).
-- (verificacoes que o CHECK nao cobre - alternativa da propria questao, nota
-- dentro da escala da questao - ficam no RespostaService, documentadas no README)
CREATE TABLE resposta(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    avaliacao_usuario_id BIGINT        NOT NULL,
    questao_id           BIGINT        NOT NULL,
    alternativa_id       BIGINT        NULL,
    nota                 SMALLINT      NULL,
    texto                VARCHAR(1000) NULL,
    CONSTRAINT fk_res_au          FOREIGN KEY (avaliacao_usuario_id) REFERENCES avaliacao_usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_res_questao     FOREIGN KEY (questao_id)           REFERENCES questao(id)           ON DELETE RESTRICT,
    CONSTRAINT fk_res_alternativa FOREIGN KEY (alternativa_id)       REFERENCES alternativa(id)       ON DELETE RESTRICT,
    CONSTRAINT chk_res_nota CHECK (nota IS NULL OR nota BETWEEN 1 AND 5),
    CONSTRAINT chk_res_uma_forma CHECK (
        (CASE WHEN alternativa_id IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN nota           IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN texto           IS NOT NULL THEN 1 ELSE 0 END) = 1
        ),
    CONSTRAINT uq_res_unica UNIQUE (avaliacao_usuario_id, questao_id)
);

-- Indices para os relatorios
CREATE INDEX idx_res_questao      ON resposta (questao_id);
CREATE INDEX idx_res_alternativa  ON resposta (alternativa_id);
CREATE INDEX idx_au_enviada_em    ON avaliacao_usuario (enviada_em);   -- filtro de datas (R4)
CREATE INDEX idx_au_avaliacao     ON avaliacao_usuario (avaliacao_id);
CREATE INDEX idx_filme_genero     ON filme (genero_id);                -- catalogo e R1
CREATE INDEX idx_questao_tema     ON questao (tema_id);
