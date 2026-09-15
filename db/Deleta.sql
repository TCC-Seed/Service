-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Remoção das tabelas da aplicação Seed.                    --
-- --------------------------------------------------------------------------------------


BEGIN TRANSACTION;

    DROP TABLE USUARIO_IES;
    DROP TABLE FUNCIONARIO;
    DROP TABLE ESTUDANTE;
    DROP TABLE USUARIO;
    DROP TABLE IES;

    DROP DOMAIN EMAIL;

    DROP TYPE GENERO;
    DROP TYPE TIPO_USUARIO;

COMMIT;