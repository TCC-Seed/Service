-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Criação das transações da aplicação Seed dados pessoais.  --
-- --------------------------------------------------------------------------------------

BEGIN TRANSACTION;

    CREATE ROLE app_backend WITH LOGIN PASSWORD 'troque_essa_senha_em_producao';

    REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;
    REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM PUBLIC;

    GRANT USAGE ON SCHEMA public TO app_backend;

    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE IES, USUARIO, ESTUDANTE, FUNCIONARIO, USUARIO_IES TO app_backend;

    GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_backend;

COMMIT;