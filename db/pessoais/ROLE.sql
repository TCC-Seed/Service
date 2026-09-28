-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Criação das transações da aplicação Seed dados pessoais.  --
-- --------------------------------------------------------------------------------------
-- Executado por ROLE.sh, que fornece a variável psql app_backend_password.

BEGIN TRANSACTION;

    SELECT format('CREATE ROLE app_backend WITH LOGIN PASSWORD %L NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION', :'app_backend_password')
    WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_backend')
    \gexec

    ALTER ROLE app_backend WITH LOGIN PASSWORD :'app_backend_password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;

    REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;
    REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM PUBLIC;

    GRANT USAGE ON SCHEMA public TO app_backend;

    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE IES, USUARIO, ESTUDANTE, FUNCIONARIO, USUARIO_IES TO app_backend;

    GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_backend;

COMMIT;
