-- =============================================================================
-- Cine Central do Brasil - carga de dados (PostgreSQL)
-- Rodar DEPOIS do schema.sql, em banco vazio (ordem: 1 schema, 2 carga, 3 relatorios)
--
-- Volume exigido na carga (enunciado):
--   - 22 usuarios (>= 20), sendo 2 administradores
--   - 12 avaliacoes de filme (secao 10 garante 11 filmes avaliados; o FETCH
--     FIRST 10 do R2 mostra o corte de verdade. Lavoura Arcaica segue fora
--     da conta, sem resposta nenhuma)
--   - 30 questoes no banco (>= 30), 7 delas montando o instrumento de cada filme
--   - ~210 tentativas (avaliacao_usuario), ~1400 respostas
--   - 1 filme sem nenhuma resposta (Lavoura Arcaica) para provar o LEFT JOIN do R3
--   - vieses propositos para os graficos mostrarem diferencas:
--       * Cidade de Deus com media ~4.6 e Madame Sata com media ~2.4
--       * CRITICO nota 0.7 abaixo; CINEFILO 0.1 acima (RF6: grupos)
--       * notas dos ultimos 4 semanas 0.3 acima (R4 mostra subida)
--
-- Para recarregar num banco ja povoado, descomente o TRUNCATE do fim.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1) USUARIOS (22; senha '123456' para todos - login didatico sem criptografia)
-- -----------------------------------------------------------------------------
INSERT INTO usuario (nome, nome_perfil, email, senha, categoria, admin) VALUES
 ('Igor Oliveira',    'igor.o',     'igor.o@exemplo.br',    '123456', 'CINEFILO',   TRUE),
 ('Joao Betoni',      'joao.b',     'joao.b@exemplo.br',    '123456', 'CRITICO',    TRUE),
 ('Fernanda Lima',    'fernanda.l', 'fernanda.l@exemplo.br','123456', 'CRITICO',    FALSE),
 ('Rafael Torres',    'rafael.t',   'rafael.t@exemplo.br',  '123456', 'CRITICO',    FALSE),
 ('Juliana Prado',    'juliana.p',  'juliana.p@exemplo.br', '123456', 'CINEFILO',   FALSE),
 ('Marcos Vieira',    'marcos.v',   'marcos.v@exemplo.br',  '123456', 'ESPECTADOR', FALSE),
 ('Beatriz Nunes',    'beatriz.n',  'beatriz.n@exemplo.br', '123456', 'CINEFILO',   FALSE),
 ('Caio Henriques',   'caio.h',     'caio.h@exemplo.br',    '123456', 'ESPECTADOR', FALSE),
 ('Larissa Duarte',   'larissa.d',  'larissa.d@exemplo.br', '123456', 'CINEFILO',   FALSE),
 ('Pedro Gomes',      'pedro.g',    'pedro.g@exemplo.br',   '123456', 'ESPECTADOR', FALSE),
 ('Talita Silva',     'talita.s',   'talita.s@exemplo.br',  '123456', 'CRITICO',    FALSE),
 ('Gabriel Fernandes','gabriel.f',  'gabriel.f@exemplo.br', '123456', 'CINEFILO',   FALSE),
 ('Renata Castro',    'renata.c',   'renata.c@exemplo.br',  '123456', 'ESPECTADOR', FALSE),
 ('Luis Haydn',       'luis.h',     'luis.h@exemplo.br',    '123456', 'CRITICO',    FALSE),
 ('Amanda Otero',     'amanda.o',   'amanda.o@exemplo.br',  '123456', 'CINEFILO',   FALSE),
 ('Diego Zampieri',   'diego.z',    'diego.z@exemplo.br',   '123456', 'ESPECTADOR', FALSE),
 ('Helena Mota',      'helena.m',   'helena.m@exemplo.br',  '123456', 'CINEFILO',   FALSE),
 ('Felipe Rocha',     'felipe.r',   'felipe.r@exemplo.br',  '123456', 'ESPECTADOR', FALSE),
 ('Paula Kraus',      'paula.k',    'paula.k@exemplo.br',   '123456', 'CRITICO',    FALSE),
 ('Otavio Borges',    'otavio.b',   'otavio.b@exemplo.br',  '123456', 'CRITICO',    FALSE),
 ('Sofia Teles',      'sofia.t',    'sofia.t@exemplo.br',   '123456', 'CINEFILO',   FALSE),
 ('Rafaela Marchetti','rafaela.m',  'rafaela.m@exemplo.br', '123456', 'ESPECTADOR', FALSE);

-- -----------------------------------------------------------------------------
-- 2) GENEROS (9)
-- -----------------------------------------------------------------------------
INSERT INTO genero (nome) VALUES
 ('Drama'), ('Comédia'), ('Crime'), ('Ação'), ('Romance'),
 ('Musical'), ('Ficção'), ('Documentário'), ('Terror');

-- -----------------------------------------------------------------------------
-- 3) FILMES (catalogo de cinema brasileiro)
-- -----------------------------------------------------------------------------
INSERT INTO filme (titulo, ano, genero_id, duracao_min, sinopse, cadastrado_por) VALUES
 ('Central do Brasil', 1998,
     (SELECT id FROM genero WHERE nome = 'Drama'), 113,
     'Dora escreve cartas para analfabetos na estacao central do Rio e embarca com Josue, um garoto em busca do pai no sertao.',
     (SELECT id FROM usuario WHERE nome_perfil = 'igor.o')),
 ('Cidade de Deus', 2002,
     (SELECT id FROM genero WHERE nome = 'Crime'), 130,
     'A trajetoria de Buscape e dos traficantes numa favela carioca entre as decadas de 1960 e 1980.',
     (SELECT id FROM usuario WHERE nome_perfil = 'igor.o')),
 ('Tropa de Elite', 2007,
     (SELECT id FROM genero WHERE nome = 'Ação'), 115,
     'Capitao Nascimento precisa treinar os novatos da tropa antes da operacao da visita do Papa.',
     (SELECT id FROM usuario WHERE nome_perfil = 'joao.b')),
 ('O Auto da Compadecida', 2000,
     (SELECT id FROM genero WHERE nome = 'Comédia'), 76,
     'As peripécias de Joao Grilo e Chico no sertao, entre golpes, milagres e o julgamento diante de Jesus.',
     (SELECT id FROM usuario WHERE nome_perfil = 'igor.o')),
 ('Se Eu Fosse Voce', 2006,
     (SELECT id FROM genero WHERE nome = 'Comédia'), 83,
     'Um casal em crise acorda cada um no corpo do outro e precisa viver a vida do outro.',
     (SELECT id FROM usuario WHERE nome_perfil = 'joao.b')),
 ('Dois Filhos de Francisco', 2005,
     (SELECT id FROM genero WHERE nome = 'Musical'), 127,
     'A historia real de Zeze Di Camargo e Luciano e a obsessao de um pai por ver os filhos virarem dupla sertaneja.',
     (SELECT id FROM usuario WHERE nome_perfil = 'igor.o')),
 ('Lisbela e o Prisioneiro', 2003,
     (SELECT id FROM genero WHERE nome = 'Romance'), 105,
     'Lisbela sonha com as estrelas do cinema enquanto Leleu, malandro do sertao, disputa seu amor ate o dia do casamento.',
     (SELECT id FROM usuario WHERE nome_perfil = 'juliana.p')),
 ('Madame Sata', 2002,
     (SELECT id FROM genero WHERE nome = 'Drama'), 105,
     'Rio dos anos 1930: Joao Francisco dos Santos, lutador e bandido, ate virar a lenda Madame Sata.',
     (SELECT id FROM usuario WHERE nome_perfil = 'rafael.t')),
 -- sem respostas de proposito: aparece com ZERO no LEFT JOIN do relatorio 3
 ('Lavoura Arcaica', 2001,
     (SELECT id FROM genero WHERE nome = 'Drama'), 155,
     'Pedro volta a fazenda da familia e confronta o peso opressivo da tradicao. Baseado no livro de Raduan Nassar.',
     (SELECT id FROM usuario WHERE nome_perfil = 'fernanda.l'));

-- -----------------------------------------------------------------------------
-- 4) TEMAS (7 + Impressoes, que fica SEM respostas para o RIGHT JOIN do R3)
-- -----------------------------------------------------------------------------
INSERT INTO tema (nome) VALUES
 ('Avaliação Geral'), ('Roteiro'), ('Direção'), ('Atuação'),
 ('Trilha Sonora'), ('Crítica'), ('Plataforma'), ('Impressões');

-- -----------------------------------------------------------------------------
-- 5) QUESTOES: banco com 30 questoes. As 7 marcadas com "(bloco)" sao as que
-- montam o instrumento de avaliacao de cada filme (RF3: reaproveitadas entre
-- filmes) - as MESMAS que o FilmeService.montarBlocoPadrao() escolhe no Java.
-- As demais ficam disponiveis para novos formularios.
-- -----------------------------------------------------------------------------
WITH novas(tema, tipo, enunciado) AS (VALUES
 ('Avaliação Geral','ESCALA','Como você avalia o filme de modo geral?'),                          -- bloco
 ('Avaliação Geral','ESCALA','Você indicaria este filme para alguém?'),
 ('Avaliação Geral','ESCALA','Você voltaria a assistir este filme?'),
 ('Avaliação Geral','ESCALA','O filme cumpre o que a sinopse promete?'),
 ('Roteiro','ESCALA','A história prende sua atenção do início ao fim?'),                          -- bloco
 ('Roteiro','ESCALA','Os diálogos soam naturais?'),
 ('Roteiro','ESCALA','O final satisfaz o que a história prometeu?'),
 ('Roteiro','ESCALA','O ritmo da história flui bem?'),
 ('Roteiro','ESCALA','Os personagens parecem verdadeiros?'),
 ('Direção','ESCALA','Como você avalia a direção do filme?'),                                     -- bloco
 ('Direção','ESCALA','Como você avalia a fotografia?'),
 ('Direção','ESCALA','A montagem ajuda a acompanhar a história?'),
 ('Direção','ESCALA','Cenário e figurino combinam com o período do filme?'),
 ('Atuação','ESCALA','Como você avalia o desempenho dos atores?'),                                -- bloco
 ('Atuação','ESCALA','Como você avalia a atuação do protagonista?'),
 ('Atuação','ESCALA','O elenco de apoio está à altura dos protagonistas?'),
 ('Atuação','ESCALA','Você acredita nas cenas mais emocionais?'),
 ('Trilha Sonora','ESCALA','A trilha sonora combina com o clima das cenas?'),                     -- bloco
 ('Trilha Sonora','ESCALA','A trilha ajuda a criar a emoção das cenas?'),
 ('Trilha Sonora','ESCALA','Há momentos em que a música se destaca?'),
 ('Plataforma','ESCOLHA','Onde você costuma assistir a filmes brasileiros?'),                     -- bloco
 ('Plataforma','ESCOLHA','Com quem você costuma assistir a filmes?'),
 ('Plataforma','ESCOLHA','Em qual dispositivo você mais assiste filmes?'),
 ('Plataforma','ESCOLHA','Você já usou um site brasileiro de avaliações antes?'),
 ('Crítica','TEXTO','Escreva sua crítica do filme: o que funcionou e o que não funcionou.'),       -- bloco
 ('Crítica','TEXTO','O que você mudaria neste filme?'),
 ('Crítica','TEXTO','Qual cena mais marcou você? Descreva.'),
 ('Crítica','TEXTO','Se você resumisse o filme em uma frase, qual seria?'),
 ('Impressões','ESCALA','O filme mexeu com você?'),
 ('Impressões','ESCALA','Depois deste filme, você busca mais cinema brasileiro?')
)
INSERT INTO questao (enunciado, tipo, tema_id, obrigatoria)
SELECT n.enunciado, n.tipo, (SELECT id FROM tema WHERE nome = n.tema),
       (n.tipo <> 'ESCOLHA')                    -- ESCOLHA fica opcional, pode pular
FROM novas n;

-- alternativas das questoes ESCOLHA
WITH alts(enunciado, alternativa) AS (VALUES
 ('Onde você costuma assistir a filmes brasileiros?','Netflix'),
 ('Onde você costuma assistir a filmes brasileiros?','Cinema'),
 ('Onde você costuma assistir a filmes brasileiros?','TV aberta ou por assinatura'),
 ('Onde você costuma assistir a filmes brasileiros?','Prime Video'),
 ('Onde você costuma assistir a filmes brasileiros?','YouTube ou canais gratuitos'),
 ('Com quem você costuma assistir a filmes?','Sozinho(a)'),
 ('Com quem você costuma assistir a filmes?','Família'),
 ('Com quem você costuma assistir a filmes?','Amigos'),
 ('Com quem você costuma assistir a filmes?','Depende do filme'),
 ('Em qual dispositivo você mais assiste filmes?','TV'),
 ('Em qual dispositivo você mais assiste filmes?','Notebook ou computador'),
 ('Em qual dispositivo você mais assiste filmes?','Celular ou tablet'),
 ('Em qual dispositivo você mais assiste filmes?','Cinema'),
 ('Você já usou um site brasileiro de avaliações antes?','Sim'),
 ('Você já usou um site brasileiro de avaliações antes?','Não'),
 ('Você já usou um site brasileiro de avaliações antes?','Não lembro')
)
INSERT INTO alternativa (questao_id, texto)
SELECT q.id, a.alternativa
FROM alts a
JOIN questao q ON q.enunciado = a.enunciado;

-- -----------------------------------------------------------------------------
-- 6) AVALIACAO (1:1 com filme) + as 7 QUESTOES DO BLOCO em cada instrumento
-- -----------------------------------------------------------------------------
INSERT INTO avaliacao (filme_id, titulo, descricao, data_inicio)
SELECT id, titulo, 'Questionário de 7 questões sobre o filme ' || titulo, now()
FROM filme;

WITH bloco(ordem, enunciado) AS (VALUES
 (1,'Como você avalia o filme de modo geral?'),
 (2,'A história prende sua atenção do início ao fim?'),
 (3,'Como você avalia a direção do filme?'),
 (4,'Como você avalia o desempenho dos atores?'),
 (5,'A trilha sonora combina com o clima das cenas?'),
 (6,'Onde você costuma assistir a filmes brasileiros?'),
 (7,'Escreva sua crítica do filme: o que funcionou e o que não funcionou.')
)
INSERT INTO avaliacao_questao (avaliacao_id, questao_id, ordem)
SELECT a.id, q.id, b.ordem
FROM avaliacao a
CROSS JOIN bloco b
JOIN questao q ON q.enunciado = b.enunciado;

-- -----------------------------------------------------------------------------
-- 7) RF4: o instrumento vai para TODOS os usuarios (mesma regra do
-- FilmeService.cadastrar, que faz INSERT ... SELECT FROM usuario)
-- -----------------------------------------------------------------------------
INSERT INTO avaliacao_destinatario (avaliacao_id, usuario_id)
SELECT a.id, u.id
FROM avaliacao a
CROSS JOIN usuario u;

-- -----------------------------------------------------------------------------
-- 8) TENTATIVAS (avaliacao_usuario): ~85% dos usuarios respondem cada filme,
-- espalhadas em ~10 semanas (filtro de datas do R4), ~1/4 anonimas (RF6).
-- Lavoura Arcaica fica de fora: zero respostas prove o LEFT JOIN do R3.
-- -----------------------------------------------------------------------------
INSERT INTO avaliacao_usuario (avaliacao_id, usuario_id, enviada_em, anonima)
SELECT a.id,
       u.id,
       now() - (floor(random() * 70)::int * INTERVAL '1 day'),
       random() < 0.25
FROM avaliacao a
JOIN filme f ON f.id = a.filme_id AND f.titulo <> 'Lavoura Arcaica'
CROSS JOIN usuario u
WHERE random() < 0.85;

-- -----------------------------------------------------------------------------
-- 9) RESPOSTAS
-- 9a. ESCALA: nota 1-5 com os vieses do cabecalho (base do filme + categoria
--     + periodo + tema + ruido), sempre presa entre 1 e 5
-- -----------------------------------------------------------------------------
WITH nota_base(titulo, base) AS (VALUES
 ('Cidade de Deus', 4.6),
 ('O Auto da Compadecida', 4.3),
 ('Central do Brasil', 4.0),
 ('Tropa de Elite', 3.9),
 ('Dois Filhos de Francisco', 3.8),
 ('Se Eu Fosse Voce', 3.2),
 ('Lisbela e o Prisioneiro', 3.0),
 ('Madame Sata', 2.4)
)
INSERT INTO resposta (avaliacao_usuario_id, questao_id, nota)
SELECT au.id, q.id,
       GREATEST(1, LEAST(5, ROUND(
           b.base
         + CASE u.categoria WHEN 'CRITICO' THEN -0.7 WHEN 'CINEFILO' THEN 0.1 ELSE 0 END
         + CASE WHEN au.enviada_em >= now() - INTERVAL '4 weeks' THEN 0.3 ELSE 0 END
         + CASE t.nome WHEN 'Avaliação Geral' THEN 0.2 WHEN 'Trilha Sonora' THEN -0.4 ELSE 0 END
         + (random() * 1.4 - 0.7)
       )::smallint))
FROM avaliacao_usuario au
JOIN avaliacao a  ON a.id = au.avaliacao_id
JOIN filme f      ON f.id = a.filme_id
JOIN nota_base b  ON b.titulo = f.titulo
JOIN avaliacao_questao aq ON aq.avaliacao_id = a.id
JOIN questao q    ON q.id = aq.questao_id AND q.tipo = 'ESCALA'
JOIN tema t       ON t.id = q.tema_id
JOIN usuario u    ON u.id = au.usuario_id;

-- 9b. ESCOLHA: sorteia uma alternativa da propria questao
INSERT INTO resposta (avaliacao_usuario_id, questao_id, alternativa_id)
SELECT au.id, q.id, alt.id
FROM avaliacao_usuario au
JOIN avaliacao a          ON a.id = au.avaliacao_id
JOIN avaliacao_questao aq ON aq.avaliacao_id = a.id
JOIN questao q            ON q.id = aq.questao_id AND q.tipo = 'ESCOLHA'
CROSS JOIN LATERAL (
    SELECT x.id
    FROM alternativa x
    WHERE x.questao_id = q.id
    ORDER BY random()
    LIMIT 1
) alt;

-- 9c. TEXTO: ~60% das tentativas escrevem critica (frase sorteada)
INSERT INTO resposta (avaliacao_usuario_id, questao_id, texto)
SELECT au.id, q.id,
       (ARRAY[
           'Um dos melhores filmes brasileiros que já assisti; recomendo de olhos fechados.',
           'A história é bonita, mas o ritmo arrasta no meio.',
           'O elenco carrega o filme inteiro nas costas.',
           'A fotografia é linda, mas o roteiro não sustenta o final.',
           'Não é para todo mundo, mas quem gostar vai amar.',
           'Assisti por indicação e acabou entrando na minha lista de favoritos.',
           'Esperava mais pela fama; ainda assim tem cenas muito fortes.',
           'Um retrato duro e honesto do Brasil.',
           'Emocionante do começo ao fim - chorei na última cena.',
           'Boa ideia, execução mediana: vale o tempo, sem grandes surpresas.'
       ])[floor(random() * 10)::int + 1]
FROM avaliacao_usuario au
JOIN avaliacao a          ON a.id = au.avaliacao_id
JOIN avaliacao_questao aq ON aq.avaliacao_id = a.id
JOIN questao q            ON q.id = aq.questao_id AND q.tipo = 'TEXTO'
WHERE random() < 0.6;

-- -----------------------------------------------------------------------------
-- 10) FILMES ADICIONAIS: +3 filmes avaliados para o ranking (R2) ter sempre
--     mais de 10 candidados (FETCH FIRST 10 mostra o corte de verdade).
--     Lavoura Arcaica segue de proposito sem resposta (a prova do LEFT JOIN
--     do R3 fica intacta). Mesmas regras dos anteriores: instrumento com as
--     MESMAS 7 questoes do bloco, destinatarios = todos os usuarios,
--     ~85% dos usuarios respondem, notas com o mesmo padrao de vieses.
-- -----------------------------------------------------------------------------
INSERT INTO filme (titulo, ano, genero_id, duracao_min, sinopse, cadastrado_por) VALUES
 ('Ainda Estou Aqui', 2024,
     (SELECT id FROM genero WHERE nome = 'Drama'), 135,
     'Eunice Paiva transforma a perda de Rubens, desaparecido na ditadura, em forca para criar os filhos e buscar a verdade.',
     (SELECT id FROM usuario WHERE nome_perfil = 'igor.o')),
 ('O Homem que Copiava', 2003,
     (SELECT id FROM genero WHERE nome = 'Comédia'), 100,
     'Andre, operador de copiadora em Sao Paulo, segue as janelas do predio vizinho com um teleobjetivo e se prende ao plano de uma golpeira.',
     (SELECT id FROM usuario WHERE nome_perfil = 'joao.b')),
 ('Até que a Sorte os Separe', 2012,
     (SELECT id FROM genero WHERE nome = 'Comédia'), 110,
     'Tiao ganha na loteria e deixa de ser pao-duro: dez anos depois, desperdicou tudo e precisa reencontrar o jeito avaro de viver para reerguer a familia.',
     (SELECT id FROM usuario WHERE nome_perfil = 'joao.b'));

-- instrumento (1:1) so para os filmes novos
INSERT INTO avaliacao (filme_id, titulo, descricao, data_inicio)
SELECT id, titulo, 'Questionário de 7 questões sobre o filme ' || titulo, now()
FROM filme
WHERE titulo IN ('Ainda Estou Aqui', 'O Homem que Copiava', 'Até que a Sorte os Separe');

-- as MESMAS 7 questoes do bloco padrao, na mesma ordem
WITH bloco(ordem, enunciado) AS (VALUES
 (1,'Como você avalia o filme de modo geral?'),
 (2,'A história prende sua atenção do início ao fim?'),
 (3,'Como você avalia a direção do filme?'),
 (4,'Como você avalia o desempenho dos atores?'),
 (5,'A trilha sonora combina com o clima das cenas?'),
 (6,'Onde você costuma assistir a filmes brasileiros?'),
 (7,'Escreva sua crítica do filme: o que funcionou e o que não funcionou.')
)
INSERT INTO avaliacao_questao (avaliacao_id, questao_id, ordem)
SELECT a.id, q.id, b.ordem
FROM avaliacao a
JOIN filme f      ON f.id = a.filme_id
CROSS JOIN bloco b
JOIN questao q    ON q.enunciado = b.enunciado
WHERE f.titulo IN ('Ainda Estou Aqui', 'O Homem que Copiava', 'Até que a Sorte os Separe');

-- RF4: instrumento dos filmes novos vai para todos os usuarios
INSERT INTO avaliacao_destinatario (avaliacao_id, usuario_id)
SELECT a.id, u.id
FROM avaliacao a
JOIN filme f  ON f.id = a.filme_id
CROSS JOIN usuario u
WHERE f.titulo IN ('Ainda Estou Aqui', 'O Homem que Copiava', 'Até que a Sorte os Separe');

-- tentativas dos filmes novos: usuarios JA EXISTENTES, mesma regra (85%,
-- espalhadas em ~10 semanas, ~1/4 anonimas)
INSERT INTO avaliacao_usuario (avaliacao_id, usuario_id, enviada_em, anonima)
SELECT a.id,
       u.id,
       now() - (floor(random() * 70)::int * INTERVAL '1 day'),
       random() < 0.25
FROM avaliacao a
JOIN filme f  ON f.id = a.filme_id
CROSS JOIN usuario u
WHERE f.titulo IN ('Ainda Estou Aqui', 'O Homem que Copiava', 'Até que a Sorte os Separe')
  AND random() < 0.85;

-- 10a. ESCALA: mesmo padrao de vieses da secao 9a (base do filme + categoria
--      + periodo + tema + ruido)
WITH nota_base(titulo, base) AS (VALUES
 ('Ainda Estou Aqui', 4.5),
 ('O Homem que Copiava', 3.6),
 ('Até que a Sorte os Separe', 3.5)
)
INSERT INTO resposta (avaliacao_usuario_id, questao_id, nota)
SELECT au.id, q.id,
       GREATEST(1, LEAST(5, ROUND(
           b.base
         + CASE u.categoria WHEN 'CRITICO' THEN -0.7 WHEN 'CINEFILO' THEN 0.1 ELSE 0 END
         + CASE WHEN au.enviada_em >= now() - INTERVAL '4 weeks' THEN 0.3 ELSE 0 END
         + CASE t.nome WHEN 'Avaliação Geral' THEN 0.2 WHEN 'Trilha Sonora' THEN -0.4 ELSE 0 END
         + (random() * 1.4 - 0.7)
       )::smallint))
FROM avaliacao_usuario au
JOIN avaliacao a  ON a.id = au.avaliacao_id
JOIN filme f      ON f.id = a.filme_id
JOIN nota_base b  ON b.titulo = f.titulo
JOIN avaliacao_questao aq ON aq.avaliacao_id = a.id
JOIN questao q    ON q.id = aq.questao_id AND q.tipo = 'ESCALA'
JOIN tema t       ON t.id = q.tema_id
JOIN usuario u    ON u.id = au.usuario_id;

-- 10b. ESCOLHA: sorteia uma alternativa da propria questao
INSERT INTO resposta (avaliacao_usuario_id, questao_id, alternativa_id)
SELECT au.id, q.id, alt.id
FROM avaliacao_usuario au
JOIN avaliacao a          ON a.id = au.avaliacao_id
JOIN filme f              ON f.id = a.filme_id
JOIN avaliacao_questao aq ON aq.avaliacao_id = a.id
JOIN questao q            ON q.id = aq.questao_id AND q.tipo = 'ESCOLHA'
CROSS JOIN LATERAL (
    SELECT x.id
    FROM alternativa x
    WHERE x.questao_id = q.id
    ORDER BY random()
    LIMIT 1
) alt
WHERE f.titulo IN ('Ainda Estou Aqui', 'O Homem que Copiava', 'Até que a Sorte os Separe');

-- 10c. TEXTO: ~60% das tentativas escrevem critica (mesmas frases da secao 9c)
INSERT INTO resposta (avaliacao_usuario_id, questao_id, texto)
SELECT au.id, q.id,
       (ARRAY[
           'Um dos melhores filmes brasileiros que já assisti; recomendo de olhos fechados.',
           'A história é bonita, mas o ritmo arrasta no meio.',
           'O elenco carrega o filme inteiro nas costas.',
           'A fotografia é linda, mas o roteiro não sustenta o final.',
           'Não é para todo mundo, mas quem gostar vai amar.',
           'Assisti por indicação e acabou entrando na minha lista de favoritos.',
           'Esperava mais pela fama; ainda assim tem cenas muito fortes.',
           'Um retrato duro e honesto do Brasil.',
           'Emocionante do começo ao fim - chorei na última cena.',
           'Boa ideia, execução mediana: vale o tempo, sem grandes surpresas.'
       ])[floor(random() * 10)::int + 1]
FROM avaliacao_usuario au
JOIN avaliacao a          ON a.id = au.avaliacao_id
JOIN filme f              ON f.id = a.filme_id
JOIN avaliacao_questao aq ON aq.avaliacao_id = a.id
JOIN questao q            ON q.id = aq.questao_id AND q.tipo = 'TEXTO'
WHERE f.titulo IN ('Ainda Estou Aqui', 'O Homem que Copiava', 'Até que a Sorte os Separe')
  AND random() < 0.6;

-- -----------------------------------------------------------------------------
-- 11) TRUNCATE para recarregar (DESCOMENTE so em banco ja povoado)
-- -----------------------------------------------------------------------------
-- TRUNCATE resposta, avaliacao_usuario, avaliacao_destinatario,
--           avaliacao_questao, avaliacao, alternativa, questao, tema,
--           filme, genero, usuario RESTART IDENTITY CASCADE;

-- -----------------------------------------------------------------------------
-- 12) CONFERENCIAS (descomente para checar depois de rodar)
-- -----------------------------------------------------------------------------
-- SELECT 'usuario' t, COUNT(*) FROM usuario            UNION ALL
-- SELECT 'genero',       COUNT(*) FROM genero          UNION ALL
-- SELECT 'filme',        COUNT(*) FROM filme           UNION ALL
-- SELECT 'tema',         COUNT(*) FROM tema            UNION ALL
-- SELECT 'questao',      COUNT(*) FROM questao         UNION ALL
-- SELECT 'alternativa',  COUNT(*) FROM alternativa     UNION ALL
-- SELECT 'avaliacao',    COUNT(*) FROM avaliacao       UNION ALL
-- SELECT 'avaliacao_questao',      COUNT(*) FROM avaliacao_questao      UNION ALL
-- SELECT 'destinatario',           COUNT(*) FROM avaliacao_destinatario UNION ALL
-- SELECT 'avaliacao_usuario',      COUNT(*) FROM avaliacao_usuario      UNION ALL
-- SELECT 'resposta',     COUNT(*) FROM resposta;

-- filme sem resposta nenhuma (esperado: Lavoura Arcaica com 0):
-- SELECT f.titulo, COUNT(DISTINCT au.id) AS tentativas, COUNT(r.id) AS respostas
-- FROM filme f
-- LEFT JOIN avaliacao a ON a.filme_id = f.id
-- LEFT JOIN avaliacao_usuario au ON au.avaliacao_id = a.id
-- LEFT JOIN resposta r ON r.avaliacao_usuario_id = au.id
-- GROUP BY f.titulo ORDER BY respostas;
