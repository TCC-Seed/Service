BEGIN TRANSACTION;

    CREATE TYPE TIPO_USUARIO AS ENUM('estudante', 'funcionario');
    CREATE TYPE GENERO AS ENUM ('feminino', 'masculino', 'nao_informar', 'outro');
    CREATE DOMAIN EMAIL AS VARCHAR(254)
        CHECK (VALUE ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

    CREATE TABLE IES (
        id SMALLSERIAL NOT NULL,
        nome VARCHAR(200) NOT NULL,
        regiao_administrativa VARCHAR(100),
        chave_vinculo VARCHAR(43) NOT NULL,
        PRIMARY KEY (id)
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

    CREATE INDEX idx_estudante_ies ON ESTUDANTE(ies_id);

    CREATE TABLE FUNCIONARIO(
        id INTEGER NOT NULL,
        nome VARCHAR(200) NOT NULL,
        formacao VARCHAR(200) NOT NULL,
        ies_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id) REFERENCES USUARIO(id) ON DELETE CASCADE,
        CONSTRAINT fk_funcionario_ies FOREIGN KEY (ies_id) REFERENCES IES(id)
    );

    CREATE INDEX idx_funcionario_ies ON FUNCIONARIO(ies_id);


COMMIT;
