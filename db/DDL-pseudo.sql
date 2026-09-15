CREATE TABLE PSEUDONIMO (
    token UUID NOT NULL DEFAULT gen_random_uuid(),
    usuario_id INTEGER NOT NULL UNIQUE,
    primary key (token, usuario_id)
);
