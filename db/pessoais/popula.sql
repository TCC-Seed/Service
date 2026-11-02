-- -----------------------------------------------------------------------------
-- Dados iniciais para desenvolvimento da aplicação Seed.
-- Pré-requisito: executar DDL.sql. Para gerar registros em auditoria.registro,
-- executar também triggers.sql antes deste script.
-- Reexecução segura: INSERTs existentes (pelas chaves primárias) são ignorados.
-- -----------------------------------------------------------------------------

BEGIN;

-- Permite que os triggers de auditoria aceitem os INSERTs quando o script for
-- executado com a role app_backend.
SELECT set_config('seed.audit_origem', 'cadastro_publico', true);

INSERT INTO ies (id, nome, chave_vinculo, regiao_administrativa) VALUES
    (1, 'Universidade de Brasília', rpad('seed-ies-1', 43, '1'), 'Plano Piloto'),
    (2, 'Instituto Federal de Brasília', rpad('seed-ies-2', 43, '2'), 'Plano Piloto'),
    (3, 'Universidade Católica de Brasília', rpad('seed-ies-3', 43, '3'), 'Taguatinga'),
    (4, 'Centro Universitário de Brasília', rpad('seed-ies-4', 43, '4'), 'Plano Piloto'),
    (5, 'Faculdade do Cerrado', rpad('seed-ies-5', 43, '5'), 'Gama')
ON CONFLICT (id) DO NOTHING;

INSERT INTO evento (id, data_inicio, data_fim, link, descricao) VALUES
    (1, '2026-11-10', '2026-11-10', 'https://seed.example/eventos/1', 'Feira de projetos de tecnologia.'),
    (2, '2026-11-15', '2026-11-16', 'https://seed.example/eventos/2', 'Encontro de saúde e bem-estar.'),
    (3, '2026-11-20', NULL, 'https://seed.example/eventos/3', 'Palestra sobre pesquisa acadêmica.'),
    (4, '2026-12-01', '2026-12-02', 'https://seed.example/eventos/4', 'Semana de inovação universitária.'),
    (5, '2026-12-05', '2026-12-05', 'https://seed.example/eventos/5', 'Oficina de planejamento de estudos.')
ON CONFLICT (id) DO NOTHING;

INSERT INTO categoria_evento (id_evento, categoria) VALUES
    (1, 'tecnologia'), (2, 'bem-estar'), (3, 'pesquisa'), (4, 'inovacao'), (5, 'educacao')
ON CONFLICT DO NOTHING;

INSERT INTO evento_ies (id_ies, id_evento) VALUES
    (1, 1), (2, 2), (3, 3), (4, 4), (5, 5)
ON CONFLICT DO NOTHING;

-- Os usuários de 1 a 5 são estudantes; de 6 a 10, funcionários.
-- A senha dos usuários de demonstração é "password" (hash BCrypt).
INSERT INTO usuario (id, email, username, senha, tipo, token) VALUES
    (1, 'ana.silva@example.com', 'ana.silva', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'estudante', '10000000-0000-4000-8000-000000000001'),
    (2, 'bruno.souza@example.com', 'bruno.souza', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'estudante', '10000000-0000-4000-8000-000000000002'),
    (3, 'carla.lima@example.com', 'carla.lima', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'estudante', '10000000-0000-4000-8000-000000000003'),
    (4, 'diego.alves@example.com', 'diego.alves', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'estudante', '10000000-0000-4000-8000-000000000004'),
    (5, 'elisa.moura@example.com', 'elisa.moura', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'estudante', '10000000-0000-4000-8000-000000000005'),
    (6, 'fabio.rocha@example.com', 'fabio.rocha', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'funcionario', '20000000-0000-4000-8000-000000000006'),
    (7, 'giovana.reis@example.com', 'giovana.reis', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'funcionario', '20000000-0000-4000-8000-000000000007'),
    (8, 'heitor.costa@example.com', 'heitor.costa', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'funcionario', '20000000-0000-4000-8000-000000000008'),
    (9, 'isabela.ferraz@example.com', 'isabela.ferraz', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'funcionario', '20000000-0000-4000-8000-000000000009'),
    (10, 'joao.pires@example.com', 'joao.pires', '$argon2id$v=19$m=16384,t=2,p=1$qqO88LwrUA3w4/GcylTS0A$dcQ5CG7xay6CPXwX4u2qg7MrrMKsrPLMz4r14NDGXiw', 'funcionario', '20000000-0000-4000-8000-000000000010')
ON CONFLICT (id) DO NOTHING;

INSERT INTO estudante (id, nome, matricula, nascimento, genero, pais_origem, id_ies) VALUES
    (1, 'Ana Silva', '20260001', '2003-02-14', 'feminino', 'Brasil', 1),
    (2, 'Bruno Souza', '20260002', '2002-07-09', 'masculino', 'Brasil', 2),
    (3, 'Carla Lima', '20260003', '2004-01-22', 'feminino', 'Brasil', 3),
    (4, 'Diego Alves', '20260004', '2001-11-03', 'masculino', 'Brasil', 4),
    (5, 'Elisa Moura', '20260005', '2003-05-30', 'nao_informar', 'Portugal', 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO funcionario (id, nome, formacao, id_ies) VALUES
    (6, 'Fabio Rocha', 'Psicologia', 1),
    (7, 'Giovana Reis', 'Pedagogia', 2),
    (8, 'Heitor Costa', 'Educação Física', 3),
    (9, 'Isabela Ferraz', 'Nutrição', 4),
    (10, 'João Pires', 'Administração', 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO formulario_base
    (id, id_estudante, data_preenchimento, anotacao, secao_autoconhecimento, secao_hidratacao,
     secao_exercicios, secao_alimentacao, secao_academia, secao_mental, secao_trabalho,
     secao_social, secao_sono, secao_lazer, secao_cultura, secao_financeiro, secao_amoroso, secao_acess)
VALUES
    (1, 1, '2026-10-20', 'Avaliação inicial.', TRUE, TRUE, FALSE, TRUE, FALSE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, FALSE, FALSE),
    (2, 2, '2026-10-21', 'Acompanhamento mensal.', TRUE, FALSE, TRUE, TRUE, TRUE, FALSE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, FALSE),
    (3, 3, '2026-10-22', 'Rotina acadêmica.', FALSE, TRUE, FALSE, TRUE, FALSE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE),
    (4, 4, '2026-10-23', 'Plano de bem-estar.', TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE, FALSE, FALSE),
    (5, 5, '2026-10-24', 'Revisão semestral.', TRUE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO formulario_personalizado (id, titulo, id_ies) VALUES
    (1, 'Hábitos de estudo', 1), (2, 'Bem-estar semanal', 2),
    (3, 'Vida acadêmica', 3), (4, 'Rotina e descanso', 4),
    (5, 'Avaliação de atividades', 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO pergunta_formulario_personalizado (id, enunciado) VALUES
    (1, 'Quantas horas você estudou nesta semana?'),
    (2, 'Como você avalia sua qualidade de sono?'),
    (3, 'Que atividade ajudou mais no seu bem-estar?'),
    (4, 'Você conseguiu organizar suas tarefas acadêmicas?'),
    (5, 'Que tema gostaria de abordar no próximo encontro?'),
    (6, 'O que poderia melhorar nas atividades oferecidas?')
ON CONFLICT (id) DO NOTHING;

INSERT INTO formulario_pergunta (id_formulario_personalizado, id_pergunta) VALUES
    (1, 1), (1, 2), (2, 1), (2, 3), (3, 2), (3, 4),
    (4, 2), (4, 5), (5, 1), (5, 6)
ON CONFLICT DO NOTHING;

INSERT INTO resposta_formulario_personalizado (id, id_usuario, id_formulario_personalizado, data_preenchimento) VALUES
    (1, 1, 1, '2026-10-25'), (2, 2, 2, '2026-10-25'), (3, 3, 3, '2026-10-26'),
    (4, 4, 4, '2026-10-26'), (5, 5, 5, '2026-10-27'), (6, 1, 1, '2026-10-28')
ON CONFLICT (id) DO NOTHING;

INSERT INTO item_resposta_formulario_personalizado (id_resposta, id_pergunta, resposta) VALUES
    (1, 1, 'Cerca de 12 horas.'),
    (1, 2, 'Boa, na maioria dos dias.'),
    (2, 1, 'Cerca de 8 horas.'),
    (3, 2, 'Sono regular.'),
    (3, 4, 'Sim, usei um cronograma semanal.'),
    (4, 2, 'Poderia melhorar.'),
    (4, 5, 'Organização financeira para estudantes.'),
    (5, 1, 'Cerca de 10 horas.'),
    (5, 6, 'Mais atividades práticas.'),
    (6, 1, 'Cerca de 14 horas.')
ON CONFLICT DO NOTHING;

INSERT INTO publicacao (id, id_funcionario, titulo, descricao, data_publicacao) VALUES
    (1, 6, 'Dicas para uma rotina equilibrada', 'Sugestões práticas para organizar estudo e descanso.', '2026-10-20'),
    (2, 7, 'Como planejar a semana acadêmica', 'Um método simples para priorizar atividades.', '2026-10-21'),
    (3, 8, 'Movimento e saúde no campus', 'Atividades físicas acessíveis para a rotina universitária.', '2026-10-22'),
    (4, 9, 'Alimentação durante os estudos', 'Ideias para refeições e lanches equilibrados.', '2026-10-23'),
    (5, 10, 'Organização financeira estudantil', 'Princípios básicos para acompanhar despesas.', '2026-10-24')
ON CONFLICT (id) DO NOTHING;

INSERT INTO categoria_publicacao (id_publicacao, categoria) VALUES
    (1, 'bem-estar'), (2, 'educacao'), (3, 'atividade-fisica'), (4, 'alimentacao'), (5, 'financas')
ON CONFLICT DO NOTHING;

-- Mantém as sequências prontas para novos INSERTs sem ID explícito.
SELECT setval(pg_get_serial_sequence('ies', 'id'), (SELECT max(id) FROM ies), true);
SELECT setval(pg_get_serial_sequence('evento', 'id'), (SELECT max(id) FROM evento), true);
SELECT setval(pg_get_serial_sequence('usuario', 'id'), (SELECT max(id) FROM usuario), true);
SELECT setval(pg_get_serial_sequence('formulario_base', 'id'), (SELECT max(id) FROM formulario_base), true);
SELECT setval(pg_get_serial_sequence('formulario_personalizado', 'id'), (SELECT max(id) FROM formulario_personalizado), true);
SELECT setval(pg_get_serial_sequence('pergunta_formulario_personalizado', 'id'), (SELECT max(id) FROM pergunta_formulario_personalizado), true);
SELECT setval(pg_get_serial_sequence('resposta_formulario_personalizado', 'id'), (SELECT max(id) FROM resposta_formulario_personalizado), true);
SELECT setval(pg_get_serial_sequence('publicacao', 'id'), (SELECT max(id) FROM publicacao), true);

COMMIT;
