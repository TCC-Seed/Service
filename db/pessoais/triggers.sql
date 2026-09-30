-- --------------------------------------------------------------------------------------
-- Data de Criação ........: 14/09/2026                                                --
-- Autor(es) ..............: João Ginuino e Gabriela Lemos                             --
-- Versão .................: 1.0                                                       --
-- Banco de Dados .........: PostgreSQL                                                --
-- Descrição ..............: Criação da função e dos triggers de auditoria do Seed.    --
-- --------------------------------------------------------------------------------------

CREATE FUNCTION auditoria.registrar() RETURNS trigger
LANGUAGE plpgsql SECURITY DEFINER
SET search_path = pg_catalog, pg_temp
AS $$
DECLARE
    origem_ator TEXT := 'banco';
    id_ator BIGINT;
    nome_ator TEXT;
    anterior JSONB;
    novo JSONB;
    chave JSONB;
BEGIN
    -- session_user preserva a conta conectada mesmo sob SECURITY DEFINER.
    IF session_user = 'app_backend' THEN
        origem_ator := nullif(current_setting('seed.audit_origem', true), '');
        IF origem_ator IS NULL OR origem_ator NOT IN ('usuario', 'cadastro_publico') THEN
            RAISE EXCEPTION 'Contexto de autoria da auditoria ausente ou inválido'
                USING ERRCODE = '42501';
        END IF;
        IF origem_ator = 'usuario' THEN
            id_ator := nullif(current_setting('seed.audit_usuario_id', true), '')::BIGINT;
            nome_ator := nullif(current_setting('seed.audit_username', true), '');
            IF id_ator IS NULL OR id_ator <= 0 OR nome_ator IS NULL THEN
                RAISE EXCEPTION 'Identificação do usuário obrigatória para auditoria'
                    USING ERRCODE = '42501';
            END IF;
        END IF;
    END IF;

    IF TG_OP <> 'INSERT' THEN
        SELECT coalesce(jsonb_object_agg(key, value), '{}'::JSONB) INTO anterior
        FROM jsonb_each(to_jsonb(OLD)) WHERE key = ANY(TG_ARGV);
        chave := jsonb_build_object('id', to_jsonb(OLD) -> 'id');
    END IF;
    IF TG_OP <> 'DELETE' THEN
        SELECT coalesce(jsonb_object_agg(key, value), '{}'::JSONB) INTO novo
        FROM jsonb_each(to_jsonb(NEW)) WHERE key = ANY(TG_ARGV);
        chave := jsonb_build_object('id', to_jsonb(NEW) -> 'id');
    END IF;

    INSERT INTO auditoria.registro
        (schema_tabela, tabela, registro_id, operacao, origem, usuario_id, username,
         conta_banco, valores_anteriores, valores_novos)
    VALUES
        (TG_TABLE_SCHEMA, TG_TABLE_NAME, chave, TG_OP, origem_ator, id_ator, nome_ator,
         session_user, anterior, novo);
    RETURN NULL;
END;
$$;

REVOKE ALL ON FUNCTION auditoria.registrar() FROM PUBLIC, app_backend;

CREATE TRIGGER auditar_ies AFTER INSERT OR UPDATE OR DELETE ON public.ies
FOR EACH ROW EXECUTE FUNCTION auditoria.registrar('id', 'nome', 'regiao_administrativa');
CREATE TRIGGER auditar_usuario AFTER INSERT OR UPDATE OR DELETE ON public.usuario
FOR EACH ROW EXECUTE FUNCTION auditoria.registrar('id', 'email', 'username', 'tipo');
CREATE TRIGGER auditar_estudante AFTER INSERT OR UPDATE OR DELETE ON public.estudante
FOR EACH ROW EXECUTE FUNCTION auditoria.registrar('id', 'nome', 'matricula', 'nascimento', 'genero', 'pais_origem', 'ies_id');
CREATE TRIGGER auditar_funcionario AFTER INSERT OR UPDATE OR DELETE ON public.funcionario
FOR EACH ROW EXECUTE FUNCTION auditoria.registrar('id', 'nome', 'formacao', 'ies_id');

