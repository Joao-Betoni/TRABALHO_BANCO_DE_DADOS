# Central do Brasil — Visualização do Banco (Mermaid)

Como visualizar:
- **VS Code**: instalar a extensão "Markdown Preview Mermaid Support" e abrir o preview (`cmd+shift+V`).
- **GitHub**: o GitHub renderiza Mermaid nativamente em qualquer `.md`.
- **Online**: colar o código em https://mermaid.live

---

## 1. DER — Esquema Relacional

```mermaid
erDiagram
    GENERO ||--o{ FILME : "categoriza"

    USUARIO ||--o{ FILME : "cadastrou"

    FILME ||--o| AVALIACAO : "possui 1:1 (instrumento)"
    AVALIACAO ||--o{ AVALIACAO_QUESTAO : "agrupa"
    AVALIACAO_QUESTAO }o--|| QUESTAO : "referencia"
    QUESTAO ||--o{ AVALIACAO_QUESTAO : "aparece em varias"
    TEMA ||--o{ QUESTAO : "classifica"
    QUESTAO ||--o{ ALTERNATIVA : "oferece"

    AVALIACAO ||--o{ AVALIACAO_DESTINATARIO : "atribuida a"
    USUARIO ||--o{ AVALIACAO_DESTINATARIO : "deve responder"

    AVALIACAO ||--o{ AVALIACAO_USUARIO : "recebe"
    USUARIO ||--o{ AVALIACAO_USUARIO : "avalia 1x por filme"

    AVALIACAO_USUARIO ||--o{ RESPOSTA : "registra"
    QUESTAO ||--o{ RESPOSTA : "respondida por"
    RESPOSTA }o--o| ALTERNATIVA : "se escolhida"

    FILME {
        bigint id PK
        varchar titulo
        smallint ano
        bigint genero_id FK
        smallint duracao_min
        text sinopse
        bigint cadastrado_por FK
        timestamp cadastrado_em
    }

    GENERO {
        bigint id PK
        varchar nome UK
    }

    USUARIO {
        bigint id PK
        varchar nome
        varchar nome_perfil UK
        varchar email UK
        varchar senha
        varchar categoria "ESPECTADOR|CINEFILO|CRITICO"
        boolean admin
    }

    AVALIACAO {
        bigint id PK
        bigint filme_id FK, UK
        varchar titulo
        text descricao
        timestamp data_inicio
        timestamp data_fim
    }

    AVALIACAO_QUESTAO {
        bigint avaliacao_id PK, FK
        bigint questao_id PK, FK
        smallint ordem UK
    }

    AVALIACAO_DESTINATARIO {
        bigint avaliacao_id PK, FK
        bigint usuario_id PK, FK
        timestamp atribuida_em
    }

    TEMA {
        bigint id PK
        varchar nome UK
    }

    QUESTAO {
        bigint id PK
        text enunciado
        varchar tipo "ESCALA|MULTIPLA_ESCOLHA|TEXTO"
        bigint tema_id FK
        smallint escala_min
        smallint escala_max
        boolean obrigatoria
    }

    ALTERNATIVA {
        bigint id PK
        bigint questao_id FK
        varchar texto
        boolean correta
    }

    AVALIACAO_USUARIO {
        bigint id PK
        bigint avaliacao_id FK
        bigint usuario_id FK
        timestamp enviada_em
        boolean anonima
    }

    RESPOSTA {
        bigint id PK
        bigint avaliacao_usuario_id FK
        bigint questao_id FK
        bigint alternativa_id FK "nullable"
        smallint nota "nullable"
        varchar texto "nullable"
    }
```

> Restrições importantes do schema:
> - `FILME`: `UNIQUE (titulo, ano)` — não tem filme duplicado
> - `AVALIACAO_USUARIO`: `UNIQUE (avaliacao_id, usuario_id)` — cada usuário avalia cada filme **uma vez**
> - `RESPOSTA`: `CHECK` exatamente um dos três campos preenchido (nota **ou** alternativa **ou** texto)

---

## 1b. DER — Diagrama Entidade-Relacionamento (notação de Chen)

Mesmo esquema da seção 1, desenhado em notação clássica (entidade = retângulo,
relacionamento = losango, atributo = elipse). Como visualizar: igual às outras
seções (preview de Markdown no VS Code, GitHub ou mermaid.live).

> **Legenda**: notação de Chen. As tabelas associativas `avaliacao_questao` e
> `avaliacao_destinatario` aparecem como **losangos** (relacionamentos N:N com
> atributo próprio); as FKs **não** são listadas como atributos porque cada
> laço já é expresso pelo losango correspondente.

```mermaid
flowchart TB
    %% ==================== ESTILOS ====================
    classDef entidade fill:#dbeafe,stroke:#1d4ed8,stroke-width:2px,color:#0f172a,font-weight:bold
    classDef relacionamento fill:#fef3c7,stroke:#d97706,stroke-width:2px,color:#0f172a
    classDef atributo fill:#f8fafc,stroke:#64748b,stroke-width:1px,color:#334155
    classDef pk fill:#f1f5f9,stroke:#0f172a,stroke-width:1.5px,color:#0f172a,font-weight:bold

    %% ==================== ENTIDADES ====================
    USUARIO["USUARIO"]:::entidade
    GENERO["GENERO"]:::entidade
    FILME["FILME"]:::entidade
    TEMA["TEMA"]:::entidade
    QUESTAO["QUESTAO"]:::entidade
    ALTERNATIVA["ALTERNATIVA"]:::entidade
    AVALIACAO["AVALIACAO"]:::entidade
    AVALIACAO_USUARIO["AVALIACAO_USUARIO"]:::entidade
    RESPOSTA["RESPOSTA"]:::entidade

    %% ==================== RELACIONAMENTOS (LOSANGOS) ====================
    R_CADASTRA{"cadastra"}:::relacionamento
    R_CLASSIFICA{"classifica"}:::relacionamento
    R_POSSUI_AVAL{"possui 1:1"}:::relacionamento
    R_AGRUPA{"agrupa"}:::relacionamento
    R_TEM_ALT{"tem"}:::relacionamento
    R_COMPOE{"avaliacao_questao"}:::relacionamento
    R_DIRECIONA{"avaliacao_destinatario"}:::relacionamento
    R_ENVIA{"envia"}:::relacionamento
    R_RECEBE{"recebe"}:::relacionamento
    R_CONTEM{"contem"}:::relacionamento
    R_REF_QUESTAO{"responde"}:::relacionamento
    R_ESCOLHE{"escolhe"}:::relacionamento

    %% ==================== ATRIBUTOS (ELIPSES) ====================
    %% USUARIO
    U_ID(["**id**"]):::pk
    U_NOME(["nome"]):::atributo
    U_PERFIL(["nome_perfil"]):::atributo
    U_EMAIL(["email"]):::atributo
    U_SENHA(["senha"]):::atributo
    U_CAT(["categoria"]):::atributo
    U_ADM(["admin"]):::atributo

    USUARIO --- U_ID
    USUARIO --- U_NOME
    USUARIO --- U_PERFIL
    USUARIO --- U_EMAIL
    USUARIO --- U_SENHA
    USUARIO --- U_CAT
    USUARIO --- U_ADM

    %% GENERO
    G_ID(["**id**"]):::pk
    G_NOME(["nome"]):::atributo

    GENERO --- G_ID
    GENERO --- G_NOME

    %% FILME
    F_ID(["**id**"]):::pk
    F_TIT(["titulo"]):::atributo
    F_ANO(["ano"]):::atributo
    F_DUR(["duracao_min"]):::atributo
    F_SIN(["sinopse"]):::atributo
    F_CAD(["cadastrado_em"]):::atributo

    FILME --- F_ID
    FILME --- F_TIT
    FILME --- F_ANO
    FILME --- F_DUR
    FILME --- F_SIN
    FILME --- F_CAD

    %% TEMA
    T_ID(["**id**"]):::pk
    T_NOME(["nome"]):::atributo

    TEMA --- T_ID
    TEMA --- T_NOME

    %% QUESTAO
    Q_ID(["**id**"]):::pk
    Q_ENUN(["enunciado"]):::atributo
    Q_TIPO(["tipo"]):::atributo
    Q_MIN(["escala_min"]):::atributo
    Q_MAX(["escala_max"]):::atributo
    Q_OBR(["obrigatoria"]):::atributo

    QUESTAO --- Q_ID
    QUESTAO --- Q_ENUN
    QUESTAO --- Q_TIPO
    QUESTAO --- Q_MIN
    QUESTAO --- Q_MAX
    QUESTAO --- Q_OBR

    %% ALTERNATIVA
    ALT_ID(["**id**"]):::pk
    ALT_TXT(["texto"]):::atributo
    ALT_COR(["correta"]):::atributo

    ALTERNATIVA --- ALT_ID
    ALTERNATIVA --- ALT_TXT
    ALTERNATIVA --- ALT_COR

    %% AVALIACAO
    AV_ID(["**id**"]):::pk
    AV_TIT(["titulo"]):::atributo
    AV_DESC(["descricao"]):::atributo
    AV_INI(["data_inicio"]):::atributo
    AV_FIM(["data_fim"]):::atributo

    AVALIACAO --- AV_ID
    AVALIACAO --- AV_TIT
    AVALIACAO --- AV_DESC
    AVALIACAO --- AV_INI
    AVALIACAO --- AV_FIM

    %% AVALIACAO_USUARIO
    AU_ID(["**id**"]):::pk
    AU_ENV(["enviada_em"]):::atributo
    AU_ANON(["anonima"]):::atributo

    AVALIACAO_USUARIO --- AU_ID
    AVALIACAO_USUARIO --- AU_ENV
    AVALIACAO_USUARIO --- AU_ANON

    %% RESPOSTA
    RES_ID(["**id**"]):::pk
    RES_NOTA(["nota"]):::atributo
    RES_TXT(["texto"]):::atributo

    RESPOSTA --- RES_ID
    RESPOSTA --- RES_NOTA
    RESPOSTA --- RES_TXT

    %% ATRIBUTOS DE RELACIONAMENTO (N:N)
    AQ_ORD(["ordem"]):::atributo
    AD_ATR(["atribuida_em"]):::atributo

    R_COMPOE --- AQ_ORD
    R_DIRECIONA --- AD_ATR

    %% ==================== LIGACOES E CARDINALIDADES ====================
    %% Usuario 1:N Filme (cadastrado_por)
    USUARIO ---|"1"| R_CADASTRA
    R_CADASTRA ---|"N"| FILME

    %% Genero 1:N Filme (genero_id)
    GENERO ---|"1"| R_CLASSIFICA
    R_CLASSIFICA ---|"N"| FILME

    %% Filme 1:1 Avaliacao (filme_id UNIQUE)
    FILME ---|"1"| R_POSSUI_AVAL
    R_POSSUI_AVAL ---|"1"| AVALIACAO

    %% Tema 1:N Questao (tema_id)
    TEMA ---|"1"| R_AGRUPA
    R_AGRUPA ---|"N"| QUESTAO

    %% Questao 1:N Alternativa (questao_id)
    QUESTAO ---|"1"| R_TEM_ALT
    R_TEM_ALT ---|"N"| ALTERNATIVA

    %% Avaliacao N:N Questao (tabela avaliacao_questao, atributo ordem)
    AVALIACAO ---|"N"| R_COMPOE
    R_COMPOE ---|"N"| QUESTAO

    %% Avaliacao N:N Usuario (tabela avaliacao_destinatario, atribuiada_em)
    AVALIACAO ---|"N"| R_DIRECIONA
    R_DIRECIONA ---|"N"| USUARIO

    %% Usuario 1:N Avaliacao_Usuario e Avaliacao 1:N Avaliacao_Usuario
    USUARIO ---|"1"| R_ENVIA
    R_ENVIA ---|"N"| AVALIACAO_USUARIO
    AVALIACAO ---|"1"| R_RECEBE
    R_RECEBE ---|"N"| AVALIACAO_USUARIO

    %% Avaliacao_Usuario 1:N Resposta
    AVALIACAO_USUARIO ---|"1"| R_CONTEM
    R_CONTEM ---|"N"| RESPOSTA

    %% Questao 1:N Resposta
    QUESTAO ---|"1"| R_REF_QUESTAO
    R_REF_QUESTAO ---|"N"| RESPOSTA

    %% Alternativa 0..1 : Resposta (alternativa_id NULL nas respostas ESCALA/TEXTO)
    ALTERNATIVA ---|"0..1"| R_ESCOLHE
    R_ESCOLHE ---|"N"| RESPOSTA
```

> As chaves primárias estão destacadas com negrito. O `alternativa_id` de
> `RESPOSTA` também fica expresso pelo losango "escolhe", não como atributo.

---

## 2. Fluxo A — Cadastro de filme (1 transação, 4 tabelas)

```mermaid
flowchart TD
    A[tela: filme-form<br/>dados do filme + elenco] --> B{titulo + ano<br/>já existe?}
    B -- sim --> C[mensagem amigável<br/>com link para o filme]
    B -- nao --> D[BEGIN TRANSACTION]
    D --> E["1. INSERT filme"]
    E --> F["2. INSERT avaliacao (1:1)"]
    F --> G["3. INSERT avaliacao_questao<br/>reaproveita questões-padrão (RF3)"]
    G --> H["4. INSERT avaliacao_destinatario<br/>destino: TODOS os usuários (RF4)"]
    H --> I{algum passo falhou?}
    I -- sim --> J[ROLLBACK<br/>nada fica salvo]
    I -- nao --> K[COMMIT ✅]

    style J fill:#f9d0d0
    style K fill:#d0f9d0
```

---

## 3. Fluxo B — Usuário avalia o filme (1 transação, 2 tabelas)

```mermaid
flowchart TD
    A[tela: responder<br/>estrelas por tema + texto<br/>checkbox publicar como anônimo] --> B{validações<br/>no Service}
    B -- questao obrigatoria sem resposta --> C[bloca e avisa na tela]
    B -- nota fora de escala_min..max --> C
    B -- ok --> D[BEGIN TRANSACTION]
    D --> E["1. INSERT avaliacao_usuario<br/>UNIQUE (avaliacao, usuario)<br/>guarda flag anonima"]
    E --> F["2. INSERT resposta × N<br/>(batchUpdate, na mesma transação)"]
    F --> G{algum passo falhou?}
    G -- sim --> H[ROLLBACK]
    G -- nao --> I[COMMIT ✅]
    I --> J[perfil mostra a avaliação<br/>ou Anônimo 🔒, conforme a flag]

    style C fill:#f9e8a0
    style H fill:#f9d0d0
    style I fill:#d0f9d0
```

---

## 4. Fluxo C — Leitura dos relatórios (RF7)

```mermaid
flowchart LR
    RESPOSTA[Resposta<br/>nota 1 a 5] --> AVAL_USR[AvaliacaoUsuario<br/>quem/quando/anonimo]
    AVAL_USR --> FILME[Filme + Genero<br/>o que foi avaliado]
    AVAL_USR --> USUARIO[Usuario<br/>categoria = grupo]

    R1[R1 Temas por gênero<br/>JOIN · GROUP BY · HAVING · IN<br/>gráfico: barras] --> FILME
    R2[R2 Top 10 filmes<br/>subconsulta · RANK · FETCH FIRST · ILIKE<br/>gráfico: barras horizontais] --> FILME
    R3[R3 Sem avaliação<br/>LEFT JOIN / RIGHT JOIN<br/>gráfico: barras com zeros] --> FILME
    R4[R4 Evolução semanal<br/>date_trunc · datas · IN<br/>gráfico: linhas] --> USUARIO
    R5[R5 Temas criticados<br/>BETWEEN · RANK · HAVING<br/>gráfico: barras] --> RESPOSTA
```

| Relatório | Perspectiva | Recursos SQL |
|---|---|---|
| R1 | Tema × gênero do filme | JOIN, GROUP BY/HAVING, IN |
| R2 | Ranking de filmes | subconsulta, RANK(), FETCH FIRST, ILIKE |
| R3 | Filmes/questões ausentes | LEFT JOIN, RIGHT JOIN |
| R4 | Períodos e grupos | date_trunc, filtro de datas, IN |
| R5 | Aspectos mais criticados | BETWEEN, RANK(), HAVING |
