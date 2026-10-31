-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Criação das tabelas da aplicação Seed dados pessoais.     --
-- --------------------------------------------------------------------------------------

BEGIN TRANSACTION;

-----------------------------------------------------------
-- Tipos

    CREATE TYPE TIPO_USUARIO AS ENUM('estudante', 'funcionario');
    CREATE TYPE GENERO AS ENUM ('feminino', 'masculino', 'nao_informar', 'outro');
    CREATE DOMAIN EMAIL AS VARCHAR(254) CHECK (VALUE ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');


-----------------------------------------------------------
-- Tabelas

    CREATE TABLE IES (
        id SMALLSERIAL NOT NULL,
        nome VARCHAR(200) NOT NULL,
        regiao_administrativa VARCHAR(100),
        PRIMARY KEY (id)
    );

    CREATE TABLE EVENTO (
        id SERIAL NOT NULL,
        data_inicio DATE NOT NULL,
        data_fim DATE,
        link VARCHAR(500),
        descricao TEXT,
        PRIMARY KEY (id),
        CHECK (data_fim IS NULL OR data_fim >= data_inicio)
    );

    CREATE TABLE CATEGORIA_EVENTO (
        id_evento INTEGER NOT NULL,
        categoria VARCHAR(100) NOT NULL,
        PRIMARY KEY (id_evento, categoria),
        FOREIGN KEY (id_evento) REFERENCES EVENTO (id) ON DELETE CASCADE
    );

    CREATE TABLE EVENTO_IES (
        id_ies SMALLINT NOT NULL,
        id_evento INTEGER NOT NULL,
        PRIMARY KEY (id_ies, id_evento),
        FOREIGN KEY (id_ies) REFERENCES IES (id) ON DELETE CASCADE,
        FOREIGN KEY (id_evento) REFERENCES EVENTO (id) ON DELETE CASCADE
    );

    CREATE TABLE USUARIO(
        id SERIAL NOT NULL,
        email EMAIL NOT NULL,
        username VARCHAR(50) NOT NULL,
        senha TEXT NOT NULL,
        tipo TIPO_USUARIO NOT NULL,
        token UUID NOT NULL DEFAULT gen_random_uuid(),
        PRIMARY KEY (id),
        UNIQUE (email),
        CONSTRAINT uk_usuario_username UNIQUE (username)
    );

    CREATE TABLE ESTUDANTE (
        id INTEGER NOT NULL,
        nome VARCHAR(200) NOT NULL,
        matricula VARCHAR(30) NOT NULL,
        nascimento DATE NOT NULL,
        genero GENERO,
        pais_origem VARCHAR(50) NOT NULL,
        ies_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id) REFERENCES USUARIO(id) ON DELETE CASCADE,
        CONSTRAINT fk_estudante_ies FOREIGN KEY (ies_id) REFERENCES IES(id),
        UNIQUE (matricula)
    );

    CREATE TABLE FUNCIONARIO(
        id INTEGER NOT NULL,
        nome VARCHAR(200) NOT NULL,
        formacao VARCHAR(200) NOT NULL,
        ies_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id) REFERENCES USUARIO(id) ON DELETE CASCADE,
        CONSTRAINT fk_funcionario_ies FOREIGN KEY (ies_id) REFERENCES IES(id)
    );

    CREATE TABLE FORMULARIO_BASE (
        id_base SERIAL NOT NULL,
        id_estudante INTEGER NOT NULL,
        data DATE NOT NULL DEFAULT CURRENT_DATE,
        anotacao TEXT,
        secao_autoconhecimento BOOLEAN NOT NULL DEFAULT FALSE,
        secao_hidratacao BOOLEAN NOT NULL DEFAULT FALSE,
        secao_exercicios BOOLEAN NOT NULL DEFAULT FALSE,
        secao_alimentacao BOOLEAN NOT NULL DEFAULT FALSE,
        secao_academia BOOLEAN NOT NULL DEFAULT FALSE,
        secao_mental BOOLEAN NOT NULL DEFAULT FALSE,
        secao_trabalho BOOLEAN NOT NULL DEFAULT FALSE,
        secao_social BOOLEAN NOT NULL DEFAULT FALSE,
        secao_sono BOOLEAN NOT NULL DEFAULT FALSE,
        secao_lazer BOOLEAN NOT NULL DEFAULT FALSE,
        secao_cultura BOOLEAN NOT NULL DEFAULT FALSE,
        secao_financeiro BOOLEAN NOT NULL DEFAULT FALSE,
        secao_amoroso BOOLEAN NOT NULL DEFAULT FALSE,
        secao_acess BOOLEAN NOT NULL DEFAULT FALSE,
        PRIMARY KEY (id_base),
        FOREIGN KEY (id_estudante) REFERENCES ESTUDANTE (id) ON DELETE CASCADE
    );

    CREATE TABLE FORMULARIO_PERSONALIZADO (
        id SERIAL NOT NULL,
        titulo VARCHAR(100) NOT NULL,
        PRIMARY KEY (id)
    );

    CREATE TABLE PERGUNTA_FORMULARIO_PERSONALIZADO (
        id SERIAL NOT NULL,
        enunciado TEXT NOT NULL,
        PRIMARY KEY (id)
    );

    CREATE TABLE FORMULARIO_PERGUNTA (
        id_formulario_personalizado INTEGER NOT NULL,
        id_pergunta INTEGER NOT NULL,
        PRIMARY KEY (id_formulario_personalizado, id_pergunta),
        FOREIGN KEY (id_formulario_personalizado) REFERENCES FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE,
        FOREIGN KEY (id_pergunta) REFERENCES PERGUNTA_FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE
    );

    CREATE TABLE RESPOSTA_FORMULARIO_PERSONALIZADO (
        id SERIAL NOT NULL,
        id_usuario INTEGER NOT NULL,
        id_formulario_personalizado INTEGER NOT NULL,
        data DATE NOT NULL DEFAULT CURRENT_DATE,
        PRIMARY KEY (id),
        FOREIGN KEY (id_usuario) REFERENCES USUARIO (id) ON DELETE CASCADE,
        FOREIGN KEY (id_formulario_personalizado) REFERENCES FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE
    );

    CREATE TABLE ITEM_RESPOSTA_FORMULARIO_PERSONALIZADO (
        id_resposta INTEGER NOT NULL,
        id_pergunta INTEGER NOT NULL,
        resposta TEXT,
        PRIMARY KEY (id_resposta, id_pergunta),
        FOREIGN KEY (id_resposta) REFERENCES RESPOSTA_FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE,
        FOREIGN KEY (id_pergunta) REFERENCES FORMULARIO_PERGUNTA (id_pergunta) ON DELETE CASCADE
    );

-----------------------------------------------------------
-- Index

    CREATE INDEX idx_estudante_ies ON ESTUDANTE(ies_id);
    CREATE INDEX idx_funcionario_ies ON FUNCIONARIO(ies_id);

-----------------------------------------------------------
-- Auditoria

    CREATE SCHEMA auditoria;
    REVOKE ALL ON SCHEMA auditoria FROM PUBLIC;

    CREATE TABLE auditoria.registro (
        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        ocorrido_em TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
        transacao_id BIGINT NOT NULL DEFAULT txid_current(),
        schema_tabela TEXT NOT NULL,
        tabela TEXT NOT NULL,
        registro_id JSONB NOT NULL,
        operacao TEXT NOT NULL CHECK (operacao IN ('INSERT', 'UPDATE', 'DELETE')),
        origem TEXT NOT NULL CHECK (origem IN ('usuario', 'cadastro_publico', 'banco')),
        usuario_id BIGINT,
        username TEXT,
        conta_banco TEXT NOT NULL,
        valores_anteriores JSONB,
        valores_novos JSONB,
        CHECK ((origem = 'usuario' AND usuario_id IS NOT NULL AND username IS NOT NULL)
            OR (origem <> 'usuario' AND usuario_id IS NULL AND username IS NULL))
    );

    CREATE INDEX registro_tabela_id_idx ON auditoria.registro (schema_tabela, tabela, (registro_id ->> 'id'), id);
    CREATE INDEX registro_usuario_data_idx ON auditoria.registro (usuario_id, ocorrido_em);
    CREATE INDEX registro_data_idx ON auditoria.registro (ocorrido_em);
    REVOKE ALL ON ALL TABLES IN SCHEMA auditoria FROM PUBLIC;
    REVOKE ALL ON ALL SEQUENCES IN SCHEMA auditoria FROM PUBLIC;

COMMIT;
