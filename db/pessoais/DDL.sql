-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Criação das tabelas da aplicação Seed dados pessoais.     --
-- --------------------------------------------------------------------------------------


BEGIN TRANSACTION;

    CREATE TYPE TIPO_USUARIO AS ENUM('estudante', 'funcionario');
    CREATE TYPE GENERO AS ENUM ('feminino', 'masculino', 'nao_informar', 'outro');
    CREATE DOMAIN EMAIL AS VARCHAR(254)
        CHECK (VALUE ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

    CREATE TABLE IES (
        id SMALLSERIAL NOT NULL,
        nome VARCHAR(200) NOT NULL,
        regiao_administrativa CHAR(100),
        PRIMARY KEY (id)
    );

    CREATE TABLE USUARIO(
        id SERIAL NOT NULL,
        email EMAIL NOT NULL,
        username char(50) NOT NULL,
        senha TEXT NOT NULL,
        tipo TIPO_USUARIO NOT NULL,
        token UUID NOT NULL DEFAULT gen_random_uuid(),
        PRIMARY KEY (id)
    );

    CREATE TABLE ESTUDANTE (
        id INTEGER NOT NULL,
        nome VARCHAR(200) NOT NULL,
        matricula CHAR(30) NOT NULL,
        nascimento DATE NOT NULL,
        genero GENERO,
        pais_origem CHAR(50) NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id) REFERENCES USUARIO(id) ON DELETE CASCADE,
        UNIQUE (matricula)
    );

    CREATE TABLE FUNCIONARIO(
        id INTEGER NOT NULL,
        nome VARCHAR(200) NOT NULL,
        formacao VARCHAR(200) NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id) REFERENCES USUARIO(id) ON DELETE CASCADE
    );

    CREATE TABLE USUARIO_IES(
        ies INTEGER NOT NULL,
        usuario INTEGER NOT NULL,
        PRIMARY KEY (ies, usuario),
        FOREIGN KEY (ies) REFERENCES IES(id),
        FOREIGN KEY (usuario) REFERENCES USUARIO(id)
    );

COMMIT;