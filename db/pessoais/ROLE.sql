-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Configuração da role e das permissões da aplicação Seed.  --
-- --------------------------------------------------------------------------------------

BEGIN TRANSACTION;

    SELECT format('CREATE ROLE app_backend WITH LOGIN PASSWORD %L NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION', :'app_backend_password')
    WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_backend')
    \gexec

    ALTER ROLE app_backend WITH LOGIN PASSWORD :'app_backend_password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;

    REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;
    REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM PUBLIC;

    GRANT USAGE ON SCHEMA public TO app_backend;

    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE IES, USUARIO, ESTUDANTE, FUNCIONARIO TO app_backend;

    GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_backend;

    REVOKE ALL ON SCHEMA auditoria FROM PUBLIC, app_backend;
    REVOKE ALL ON ALL TABLES IN SCHEMA auditoria FROM PUBLIC, app_backend;
    REVOKE ALL ON ALL SEQUENCES IN SCHEMA auditoria FROM PUBLIC, app_backend;
    REVOKE TRUNCATE, TRIGGER ON public.ies, public.usuario, public.estudante, public.funcionario FROM app_backend;

COMMIT;
