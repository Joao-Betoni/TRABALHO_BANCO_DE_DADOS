-- =============================================================================
-- Cine Central do Brasil - relatorios (RF7)
-- Os 5 relatorios da plataforma; no Java o mesmo SQL vive no RelatorioDAO
-- (mudou de um lado, muda no outro). Rodar na ordem: schema.sql, carga.sql,
-- e depois estas consultas. Recursos da secao 5 do enunciado por consulta:
--
--   R1  JOIN interno, GROUP BY/HAVING, subconsulta, filtro IN
--   R2  subconsulta, RANK(), FETCH FIRST, ILIKE, GROUP BY/HAVING
--   R3  LEFT JOIN e RIGHT JOIN (juncoes externas dos dois lados)
--   R4  date_trunc (periodo), filtro de datas, filtro IN, GROUP BY, JOIN
--   R5  agregacao GROUP BY/HAVING, BETWEEN, FILTER, RANK(), FETCH FIRST
-- =============================================================================

-- ======================================================
-- RELATORIO 1: media por tema x genero de filme
-- "o publico avalia melhor o roteiro em dramas do que em comedias?"
-- Grafico: barras agrupadas (genero no eixo X, uma cor por tema)
-- ======================================================
SELECT g.nome                    AS genero,
       t.nome                    AS tema,
       ROUND(AVG(r.nota), 2)     AS media,
       COUNT(*)                  AS respostas
FROM resposta r
JOIN avaliacao_usuario au ON au.id  = r.avaliacao_usuario_id
JOIN avaliacao a          ON a.id   = au.avaliacao_id
JOIN filme f              ON f.id   = a.filme_id
JOIN genero g             ON g.id   = f.genero_id
JOIN questao q            ON q.id   = r.questao_id AND q.tipo = 'ESCALA'
JOIN tema t               ON t.id   = q.tema_id
WHERE f.genero_id IN (
    SELECT id FROM genero WHERE nome IN (
        'Drama', 'Comédia', 'Crime', 'Romance'
    )
)
GROUP BY g.nome, t.nome
HAVING COUNT(*) >= 5                     -- ignora media de poucas respostas
ORDER BY g.nome, media DESC;


-- ======================================================
-- RELATORIO 2: top 10 filmes por nota (ranking da plataforma)
-- Compara com a media geral na subconsulta.
-- ======================================================
SELECT RANK() OVER (ORDER BY m.media DESC) AS posicao,
       m.titulo,
       m.ano,
       m.genero,
       ROUND(m.media, 2)                  AS nota_media,
       ROUND((SELECT AVG(r.nota)          -- subconsulta: media geral da plataforma
              FROM resposta r
              WHERE r.nota IS NOT NULL), 2) AS media_geral
FROM (
    SELECT f.titulo, f.ano, g.nome AS genero,
           AVG(r.nota)           AS media,
           COUNT(DISTINCT au.id) AS avaliadores
    FROM resposta r
    JOIN avaliacao_usuario au ON au.id = r.avaliacao_usuario_id
    JOIN avaliacao a          ON a.id  = au.avaliacao_id
    JOIN filme f              ON f.id  = a.filme_id
    JOIN genero g             ON g.id  = f.genero_id
    WHERE f.titulo ILIKE '%'             -- na tela: '%' || busca || '%'
    GROUP BY f.id, f.titulo, f.ano, g.nome
    HAVING COUNT(DISTINCT au.id) >= 3    -- pelo menos 3 avaliacoes
) m
ORDER BY posicao
FETCH FIRST 10 ROWS ONLY;


-- ======================================================
-- RELATORIO 3: filmes sem avaliacao (LEFT JOIN) e temas sem respostas
-- (RIGHT JOIN). A carga deixa "Lavoura Arcaica" sem resposta nenhuma de
-- proposito, para aparecer com ZERO aqui e provar a juncao externa.
-- ======================================================
-- 3a. filmes com mais "abandonados" primeiro (COUNT 0 nos que ninguem viu)
SELECT f.titulo,
       COUNT(DISTINCT au.id) AS avaliadores,
       COUNT(r.id)           AS respostas
FROM filme f
LEFT JOIN avaliacao          a  ON a.filme_id = f.id
LEFT JOIN avaliacao_usuario au ON au.avaliacao_id = a.id
LEFT JOIN resposta           r  ON r.avaliacao_usuario_id = au.id
GROUP BY f.id, f.titulo
ORDER BY avaliadores ASC, respostas ASC
FETCH FIRST 10 ROWS ONLY;

-- 3b. temas e questoes que ninguem respondeu ainda (RIGHT JOIN)
SELECT t.nome              AS tema,
       COUNT(q.id)         AS questoes_cadastradas,
       COUNT(r.id)         AS respostas_recebidas
FROM resposta r
RIGHT JOIN questao q ON q.id = r.questao_id
RIGHT JOIN tema t    ON t.id = q.tema_id
GROUP BY t.nome
ORDER BY respostas_recebidas ASC;


-- ======================================================
-- RELATORIO 4: evolucao das notas por semana e por grupo de usuarios
-- "nota subiu ou caiu? espectador difere de critico?" (RF6 - grupos)
-- Grafico: linhas (uma por tema)
-- ======================================================
SELECT date_trunc('week', au.enviada_em)::date AS semana,
       t.nome                                  AS tema,
       u.categoria                             AS grupo,
       ROUND(AVG(r.nota), 2)                   AS media,
       COUNT(*)                                AS respostas
FROM resposta r
JOIN avaliacao_usuario au ON au.id = r.avaliacao_usuario_id
JOIN usuario u            ON u.id  = au.usuario_id
JOIN questao q            ON q.id  = r.questao_id AND q.tipo = 'ESCALA'
JOIN tema t               ON t.id  = q.tema_id
WHERE au.enviada_em BETWEEN now() - INTERVAL '10 weeks' AND now()   -- filtro de datas
  AND u.categoria IN ('ESPECTADOR', 'CINEFILO', 'CRITICO')          -- filtro IN (grupos)
GROUP BY semana, t.nome, u.categoria
ORDER BY semana, tema;


-- ======================================================
-- RELATORIO 5: temas mais criticados (% de notas 1 e 2)
-- "o que o publico mais rejeita nos filmes?"
-- ======================================================
SELECT RANK() OVER (ORDER BY ROUND(100.0 * COUNT(*) FILTER (WHERE r.nota BETWEEN 1 AND 2)
                                        / COUNT(*), 1) DESC) AS posicao,
       t.nome                                     AS tema,
       ROUND(AVG(r.nota), 2)                      AS media,
       ROUND(100.0 * COUNT(*) FILTER (WHERE r.nota BETWEEN 1 AND 2)
             / COUNT(*), 1)                       AS pct_criticadas,
       COUNT(*)                                   AS respostas
FROM resposta r
JOIN questao q ON q.id = r.questao_id AND q.tipo = 'ESCALA'
JOIN tema t    ON t.id = q.tema_id
GROUP BY t.nome
HAVING COUNT(*) >= 10                              -- base estatistica decente
ORDER BY posicao
FETCH FIRST 5 ROWS ONLY;
