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

CREATE TYPE TIPO_USUARIO AS ENUM ('estudante', 'funcionario');
CREATE TYPE GENERO AS ENUM ('feminino', 'masculino', 'nao_informar', 'outro');
CREATE DOMAIN EMAIL AS VARCHAR(254) CHECK (VALUE ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-----------------------------------------------------------
-- Tabelas

CREATE TABLE IES
(
    id                    SMALLSERIAL  NOT NULL,
    nome                  VARCHAR(200) NOT NULL,
    chave_vinculo         VARCHAR(43)  NOT NULL,
    regiao_administrativa VARCHAR(100),
    CONSTRAINT pk_ies PRIMARY KEY (id)
);

CREATE TABLE EVENTO
(
    id          SERIAL NOT NULL,
    data_inicio DATE   NOT NULL,
    data_fim    DATE,
    link        VARCHAR(500),
    descricao   TEXT,
    CONSTRAINT pk_evento PRIMARY KEY (id),
    CONSTRAINT ck_evento_datas CHECK (data_fim IS NULL OR data_fim >= data_inicio)
);

CREATE TABLE CATEGORIA_EVENTO
(
    id_evento INTEGER      NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    CONSTRAINT pk_categoria_evento PRIMARY KEY (id_evento, categoria),
    CONSTRAINT fk_categoria_evento_evento FOREIGN KEY (id_evento)
        REFERENCES EVENTO (id) ON DELETE CASCADE
);

CREATE TABLE EVENTO_IES
(
    id_ies    SMALLINT NOT NULL,
    id_evento INTEGER  NOT NULL,
    CONSTRAINT pk_evento_ies PRIMARY KEY (id_ies, id_evento),
    CONSTRAINT fk_evento_ies_ies FOREIGN KEY (id_ies)
        REFERENCES IES (id) ON DELETE CASCADE,
    CONSTRAINT fk_evento_ies_evento FOREIGN KEY (id_evento)
        REFERENCES EVENTO (id) ON DELETE CASCADE
);

CREATE TABLE USUARIO
(
    id       SERIAL       NOT NULL,
    email    EMAIL        NOT NULL,
    username VARCHAR(50)  NOT NULL,
    senha    TEXT         NOT NULL,
    tipo     TIPO_USUARIO NOT NULL,
    token    UUID         NOT NULL DEFAULT gen_random_uuid(),
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT uk_usuario_token UNIQUE (token)
);

CREATE TABLE ESTUDANTE
(
    id          INTEGER      NOT NULL,
    nome        VARCHAR(200) NOT NULL,
    matricula   VARCHAR(30)  NOT NULL,
    nascimento  DATE         NOT NULL,
    genero      GENERO,
    pais_origem VARCHAR(50)  NOT NULL,
    id_ies      SMALLINT     NOT NULL,
    CONSTRAINT pk_estudante PRIMARY KEY (id),
    CONSTRAINT uk_estudante_matricula UNIQUE (matricula),
    CONSTRAINT fk_estudante_usuario FOREIGN KEY (id)
        REFERENCES USUARIO (id) ON DELETE CASCADE,
    CONSTRAINT fk_estudante_ies FOREIGN KEY (id_ies)
        REFERENCES IES (id)
);

CREATE TABLE FUNCIONARIO
(
    id       INTEGER      NOT NULL,
    nome     VARCHAR(200) NOT NULL,
    formacao VARCHAR(200) NOT NULL,
    id_ies   SMALLINT     NOT NULL,
    CONSTRAINT pk_funcionario PRIMARY KEY (id),
    CONSTRAINT fk_funcionario_usuario FOREIGN KEY (id)
        REFERENCES USUARIO (id) ON DELETE CASCADE,
    CONSTRAINT fk_funcionario_ies FOREIGN KEY (id_ies)
        REFERENCES IES (id)
);

CREATE TABLE FORMULARIO_BASE
(
    id                     SERIAL  NOT NULL,
    id_estudante           INTEGER NOT NULL,
    data_preenchimento     DATE    NOT NULL DEFAULT CURRENT_DATE,
    anotacao               TEXT,
    secao_autoconhecimento BOOLEAN NOT NULL DEFAULT FALSE,
    secao_hidratacao       BOOLEAN NOT NULL DEFAULT FALSE,
    secao_exercicios       BOOLEAN NOT NULL DEFAULT FALSE,
    secao_alimentacao      BOOLEAN NOT NULL DEFAULT FALSE,
    secao_academia         BOOLEAN NOT NULL DEFAULT FALSE,
    secao_mental           BOOLEAN NOT NULL DEFAULT FALSE,
    secao_trabalho         BOOLEAN NOT NULL DEFAULT FALSE,
    secao_social           BOOLEAN NOT NULL DEFAULT FALSE,
    secao_sono             BOOLEAN NOT NULL DEFAULT FALSE,
    secao_lazer            BOOLEAN NOT NULL DEFAULT FALSE,
    secao_cultura          BOOLEAN NOT NULL DEFAULT FALSE,
    secao_financeiro       BOOLEAN NOT NULL DEFAULT FALSE,
    secao_amoroso          BOOLEAN NOT NULL DEFAULT FALSE,
    secao_acess            BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_formulario_base PRIMARY KEY (id),
    CONSTRAINT fk_formulario_base_estudante FOREIGN KEY (id_estudante)
        REFERENCES ESTUDANTE (id) ON DELETE CASCADE
);

CREATE TABLE FORMULARIO_PERSONALIZADO
(
    id                       SERIAL       NOT NULL,
    titulo                   VARCHAR(100) NOT NULL,
    id_ies                   SMALLINT     NOT NULL,
    criado_em                TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT pk_formulario_personalizado PRIMARY KEY (id),
    CONSTRAINT fk_formulario_personalizado_ies FOREIGN KEY (id_ies)
        REFERENCES IES (id)
);

CREATE TABLE PERGUNTA_FORMULARIO_PERSONALIZADO
(
    id        SERIAL NOT NULL,
    enunciado TEXT   NOT NULL,
    CONSTRAINT pk_pergunta_formulario_personalizado PRIMARY KEY (id)
);

CREATE TABLE FORMULARIO_PERGUNTA
(
    id_formulario_personalizado INTEGER NOT NULL,
    id_pergunta                 INTEGER NOT NULL,
    CONSTRAINT pk_formulario_pergunta PRIMARY KEY (id_formulario_personalizado, id_pergunta),
    CONSTRAINT fk_formulario_pergunta_formulario FOREIGN KEY (id_formulario_personalizado)
        REFERENCES FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE,
    CONSTRAINT fk_formulario_pergunta_pergunta FOREIGN KEY (id_pergunta)
        REFERENCES PERGUNTA_FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE
);

CREATE TABLE RESPOSTA_FORMULARIO_PERSONALIZADO
(
    id                          SERIAL  NOT NULL,
    id_usuario                  INTEGER NOT NULL,
    id_formulario_personalizado INTEGER NOT NULL,
    data_preenchimento          DATE    NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT pk_resposta_formulario_personalizado PRIMARY KEY (id),
    CONSTRAINT fk_resposta_formulario_usuario FOREIGN KEY (id_usuario)
        REFERENCES USUARIO (id) ON DELETE CASCADE,
    CONSTRAINT fk_resposta_formulario_formulario FOREIGN KEY (id_formulario_personalizado)
        REFERENCES FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE
);

CREATE TABLE ITEM_RESPOSTA_FORMULARIO_PERSONALIZADO
(
    id_resposta INTEGER NOT NULL,
    id_pergunta INTEGER NOT NULL,
    resposta    TEXT,
    CONSTRAINT pk_item_resposta_formulario_personalizado PRIMARY KEY (id_resposta, id_pergunta),
    CONSTRAINT fk_item_resposta_resposta FOREIGN KEY (id_resposta)
        REFERENCES RESPOSTA_FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_resposta_pergunta FOREIGN KEY (id_pergunta)
        REFERENCES PERGUNTA_FORMULARIO_PERSONALIZADO (id) ON DELETE CASCADE
);

CREATE TABLE PUBLICACAO
(
    id              SERIAL       NOT NULL,
    id_funcionario  INTEGER      NOT NULL,
    titulo          VARCHAR(200) NOT NULL,
    descricao       TEXT,
    data_publicacao DATE         NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT pk_publicacao PRIMARY KEY (id),
    CONSTRAINT fk_publicacao_funcionario FOREIGN KEY (id_funcionario)
        REFERENCES FUNCIONARIO (id) ON DELETE CASCADE
);

CREATE TABLE CATEGORIA_PUBLICACAO
(
    id_publicacao INTEGER      NOT NULL,
    categoria     VARCHAR(100) NOT NULL,
    CONSTRAINT pk_categoria_publicacao PRIMARY KEY (id_publicacao, categoria),
    CONSTRAINT fk_categoria_publicacao_publicacao FOREIGN KEY (id_publicacao)
        REFERENCES PUBLICACAO (id) ON DELETE CASCADE
);

CREATE SCHEMA auditoria;
REVOKE ALL ON SCHEMA auditoria FROM PUBLIC;

CREATE TABLE auditoria.registro
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ocorrido_em        TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    transacao_id       BIGINT      NOT NULL DEFAULT txid_current(),
    schema_tabela      TEXT        NOT NULL,
    tabela             TEXT        NOT NULL,
    registro_id        JSONB       NOT NULL,
    operacao           TEXT        NOT NULL CHECK (operacao IN ('INSERT', 'UPDATE', 'DELETE')),
    origem             TEXT        NOT NULL CHECK (origem IN ('usuario', 'cadastro_publico', 'banco')),
    usuario_id         BIGINT,
    username           TEXT,
    conta_banco        TEXT        NOT NULL,
    valores_anteriores JSONB,
    valores_novos      JSONB,
    CHECK ((origem = 'usuario' AND usuario_id IS NOT NULL AND username IS NOT NULL)
        OR (origem <> 'usuario' AND usuario_id IS NULL AND username IS NULL))
);

CREATE INDEX registro_tabela_id_idx ON auditoria.registro (schema_tabela, tabela, (registro_id ->> 'id'), id);
CREATE INDEX registro_usuario_data_idx ON auditoria.registro (usuario_id, ocorrido_em);
CREATE INDEX registro_data_idx ON auditoria.registro (ocorrido_em);
REVOKE ALL ON ALL TABLES IN SCHEMA auditoria FROM PUBLIC;
REVOKE ALL ON ALL SEQUENCES IN SCHEMA auditoria FROM PUBLIC;


-----------------------------------------------------------
-- Índices

CREATE INDEX idx_estudante_id_ies ON ESTUDANTE (id_ies);
CREATE INDEX idx_funcionario_id_ies ON FUNCIONARIO (id_ies);
CREATE INDEX idx_evento_ies_id_evento ON EVENTO_IES (id_evento);
CREATE INDEX idx_formulario_base_id_estudante ON FORMULARIO_BASE (id_estudante);
CREATE INDEX idx_formulario_personalizado_ies ON FORMULARIO_PERSONALIZADO (id_ies);
CREATE UNIQUE INDEX uq_pergunta_enunciado_normalizado
    ON PERGUNTA_FORMULARIO_PERSONALIZADO (lower(btrim(enunciado)));
CREATE INDEX idx_formulario_pergunta_id_pergunta ON FORMULARIO_PERGUNTA (id_pergunta);
CREATE INDEX idx_resposta_formulario_id_usuario ON RESPOSTA_FORMULARIO_PERSONALIZADO (id_usuario);
CREATE INDEX idx_resposta_formulario_id_formulario ON RESPOSTA_FORMULARIO_PERSONALIZADO (id_formulario_personalizado);
CREATE INDEX idx_resposta_formulario_data_id ON RESPOSTA_FORMULARIO_PERSONALIZADO (id_formulario_personalizado, data_preenchimento DESC, id DESC);
CREATE INDEX idx_item_resposta_id_pergunta ON ITEM_RESPOSTA_FORMULARIO_PERSONALIZADO (id_pergunta);
CREATE INDEX idx_publicacao_id_funcionario ON PUBLICACAO (id_funcionario);

COMMIT;
