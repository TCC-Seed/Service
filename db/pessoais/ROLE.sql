-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Configuração da role e das permissões da aplicação Seed.  --
-- --------------------------------------------------------------------------------------

-- Executar com psql. O Compose fornece SPRING_DATASOURCE_PASSWORD ao container;
-- \getenv lê essa variável sem precisar de um script shell ou senha fixa no SQL.
-- ON_ERROR_STOP interrompe a execução em caso de erro, inclusive na validação.
\set ON_ERROR_STOP on
\set app_backend_password ''
\getenv app_backend_password SPRING_DATASOURCE_PASSWORD

SELECT length(:'app_backend_password') > 0 AS senha_definida \gset
\if :senha_definida
\else
    DO $$ BEGIN
        RAISE EXCEPTION 'Defina SPRING_DATASOURCE_PASSWORD no ambiente antes de executar ROLE.sql';
    END $$;
\endif

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

\unset app_backend_password
\unset senha_definida
