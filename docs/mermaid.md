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

    PESSOA ||--o{ FILME_ATOR : "atua em"
    PESSOA ||--o{ FILME_DIRETOR : "direge"
    FILME ||--o{ FILME_ATOR : "tem"
    FILME ||--o{ FILME_DIRETOR : "tem"

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

    PESSOA {
        bigint id PK
        varchar nome UK
    }

    FILME_ATOR {
        bigint filme_id PK, FK
        bigint pessoa_id PK, FK
        varchar personagem
    }

    FILME_DIRETOR {
        bigint filme_id PK, FK
        bigint pessoa_id PK, FK
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
