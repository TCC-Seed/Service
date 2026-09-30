<div align="center">

# TCC Seed — Service

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
</div>

API REST responsável pela coleta, armazenamento e análise de dados sobre humor, hábitos e saúde geral de estudantes universitários, com autenticação via **JWT** e persistência via **JPA/Hibernate**.

---

## Sobre o Projeto

O ambiente universitário contemporâneo tem apresentado um aumento significativo nos casos relacionados a problemas de saúde entre estudantes, incluindo questões de saúde mental, sedentarismo, estresse acadêmico e hábitos prejudiciais ao bem-estar. Esses fatores impactam diretamente o desempenho estudantil e evidenciam a necessidade de mecanismos eficazes de monitoramento e intervenção.

Este serviço é o **back-end** de um aplicativo mobile voltado à coleta e análise de dados sobre o humor, hábitos e saúde geral dentro de instituições de ensino superior (IES). A proposta busca auxiliar o direcionamento de políticas institucionais de promoção à saúde, além de oferecer aos estudantes meios de buscar ajuda dentro da IES.

Este projeto foi desenvolvido como **Trabalho de Conclusão de Curso (TCC)**.

---

## Funcionalidades

- Exposição de endpoints REST para coleta e consulta de dados de saúde, humor e hábitos
- Autenticação e autorização via **JWT**
- Persistência de dados via **Spring Data JPA** sobre banco **PostgreSQL**
- Documentação interativa dos endpoints via **Swagger / OpenAPI**
- Ambiente de desenvolvimento totalmente containerizado com **Docker Compose**, incluindo hot reload
- Pipeline de **CI/CD** para build e publicação automática da imagem Docker a cada push na `main`

---

## Tecnologias Utilizadas

| Camada                  | Tecnologia                    |
| ------------------------ | ------------------------------ |
| Linguagem                | Java 21                        |
| Framework                | Spring Boot                    |
| Segurança / Autenticação | Spring Security + JWT          |
| Persistência             | Spring Data JPA (Hibernate)    |
| Banco de Dados           | PostgreSQL                     |
| Documentação da API      | SpringDoc OpenAPI (Swagger UI) |
| Build                    | Maven                          |
| Containerização          | Docker + Docker Compose        |
| CI/CD                    | GitHub Actions                 |

---

## Como Executar

### Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose
- Make *(opcional, mas recomendado)*

### Configuração

Copie o arquivo de exemplo de variáveis de ambiente e ajuste os valores conforme necessário. No Compose, a aplicação conecta como `app_backend`; defina `SPRING_DATASOURCE_PASSWORD` para a senha dessa role. Defina também `JWT_SECRET` com pelo menos 32 bytes aleatórios (por exemplo, usando `openssl rand -base64 32`):

```bash
cp .example.env .env
```

### Executando com Docker Compose

Subindo tudo em primeiro plano, com build e logs em tempo real:

```bash
make run
```

Ou, sem o Makefile:

```bash
docker compose up --build
```

O PostgreSQL executa `db/pessoais/DDL.sql`, `db/pessoais/ROLE.sql` e `db/pessoais/triggers.sql` automaticamente ao inicializar um volume de dados novo. Os scripts não são reaplicados a um volume já inicializado, para preservar os dados existentes.

### Inicializando um volume PostgreSQL existente

Se o volume `postgres-data` já existir sem o schema pessoal (tipos, domínio e tabelas), atualize primeiro o `.env` com uma senha em `SPRING_DATASOURCE_PASSWORD`. Depois execute o DDL e a role uma única vez:

```bash
docker compose up -d postgres
docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f /docker-entrypoint-initdb.d/01-DDL.sql'
docker compose exec -T postgres /docker-entrypoint-initdb.d/02-ROLE.sh
docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" --single-transaction -f /docker-entrypoint-initdb.d/03-triggers.sql'
docker compose up -d app
```

O DDL não é idempotente; não o execute novamente se algum dos tipos, domínio ou tabelas já existir. O processo não apaga nem recria o volume.

### Outros comandos disponíveis (Makefile)

```bash
make up        # sobe os containers em background
make down      # para e remove os containers (mantém dados do banco)
make build     # reconstrói as imagens
make restart   # reinicia os containers
make clean     # remove containers e volumes (reseta o banco de dados)
```

### Acessando a aplicação

| Recurso           | URL                                            |
| ------------------ | ----------------------------------------------- |
| API                 | http://localhost:8080                          |
| Swagger UI          | http://localhost:8080/swagger-ui/index.html    |
| OpenAPI (JSON)      | http://localhost:8080/v3/api-docs              |

---

## CI/CD

A cada push na branch `main`, uma pipeline do **GitHub Actions** realiza o build da imagem Docker (multi-stage, otimizada para produção) e publica automaticamente no Docker Hub, com as tags `latest` e o hash do commit correspondente.

---


<div align="center">
Feito com <3 na <strong>Universidade de Brasília</strong>
</div>


## Controle de IES

`GET /IES` é público para selecionar a instituição no cadastro. As demais rotas exigem o cookie de autenticação `seed_cookie` obtido no login.
Os dados institucionais são `id`, `nome` (obrigatório, até 200 caracteres) e
`regiaoAdministrativa` (opcional, até 100 caracteres). Nome não é identificador único.

| Método e rota | Permissão e resultado |
| --- | --- |
| `GET /IES?pagina=0&tamanho=20` | Público; lista paginada com id, nome e regiaoAdministrativa (máximo 100 por página), sem chave |
| `GET /IES/{id}` | Usuário autenticado; dados institucionais sem chave |
| `PUT /IES/{id}` | Funcionário vinculado; substitui nome e região; retorna 200 |
| `DELETE /IES/{id}` | Funcionário da IES; exclui a instituição, todos os seus estudantes, funcionários e respectivas contas; retorna 204 |
| `GET /IES/{id}/privado` | Funcionário vinculado; dados institucionais e `chaveIes`, sem cache |
| `POST /IES/{id}/chave/renovar` | Funcionário vinculado; troca a chave e retorna os dados privados, sem cache |

A atualização recebe `{"nome":"Universidade Exemplo","regiaoAdministrativa":"Brasília"}`.
A chave tem 256 bits aleatórios e nunca aparece nas respostas públicas, nem é recebida na URL.
A troca da chave impede novos ingressos com a chave antiga, sem remover funcionários já vinculados.
Não há criação de dashboard nesta entrega; futuros endpoints privados devem exigir o mesmo vínculo.

### Cadastro de estudante com uma IES obrigatória

`GET /IES?pagina=0&tamanho=20` não exige login. Cada item de `content` contém somente
`id`, `nome` e `regiaoAdministrativa`, além dos metadados de paginação da resposta.
Escolha o ID e envie em `POST /auth/cadastro/estudante`:

```json
{
  "email": "ana.silva@example.com",
  "username": "ana.silva",
  "senha": "Exemplo@2026",
  "nome": "Ana Silva",
  "matricula": "2026001234",
  "nascimento": "2001-05-14",
  "paisOrigem": "Brasil",
  "iesId": 1
}
```

`iesId` é obrigatório, positivo e deve identificar uma IES existente. Não é necessária
a chave privada de funcionário. O estudante não pode criar uma IES nem trocar seu vínculo
nesta etapa. A FK `estudante.ies_id NOT NULL` garante exatamente uma IES por estudante.
O cadastro retorna 201; ID ausente/inválido retorna 400 e IES inexistente retorna 404.
O login continua por username e senha. O vínculo estudantil não concede acesso à chave
privada nem às operações de alteração/exclusão da IES.

### Cadastro de funcionário com IES

`POST /auth/cadastro/funcionario` recebe os campos anteriores e **exatamente uma** das opções:

```json
{
  "email": "funcionario@example.com",
  "username": "funcionario",
  "senha": "exemplo",
  "nome": "Funcionário Exemplo",
  "formacao": "Psicologia",
  "novaIes": {"nome": "Universidade Exemplo", "regiaoAdministrativa": "Brasília"}
}
```

Para uma instituição existente, substitua `novaIes` por `"iesId": 1, "chaveIes": "chave-compartilhada"`.
A resposta mantém o contrato anterior de usuário criado. Após o login, consulte a listagem de IES
e os dados privados da instituição criada para obter sua chave.
Funcionário, nova IES e vínculo são persistidos juntos; falha no vínculo desfaz o cadastro.
Cada funcionário pertence a exatamente uma IES, escolhida no cadastro, através de `funcionario.ies_id NOT NULL`. Não há transferência nem ingresso posterior em outra instituição.
`POST /IES` e `POST /IES/{id}/vinculos` foram removidos. A única criação de IES acontece
em `POST /auth/cadastro/funcionario` com `novaIes`, junto do primeiro funcionário.
Não há endpoint para desvincular o último funcionário ou deixar uma IES sem funcionário.

A exclusão de uma IES remove também as contas de todos os seus estudantes e funcionários, incluindo
quem solicitou a operação. Os JWTs desses usuários deixam de autenticar nas requisições
seguintes, pois as contas não existem mais. A exclusão inteira é revertida se outros dados dependentes
impedirem a operação.

Erros: 400 para dados inválidos ou opções de cadastro incompatíveis; 401 sem autenticação;
403 para chave incorreta ou usuário sem permissão; 404 para IES inexistente;
409 quando dados dependentes impedem a exclusão. Estudantes, mesmo vinculados,
não podem acessar a chave ou alterar a IES.

### Atualização de um banco existente

Para bancos legados, execute apenas as migrações pendentes, em ordem, com a aplicação parada.
A migração `0001-chave-ies.sql` aplica-se ao modelo anterior à FK de funcionário; a consulta
abaixo também é exclusiva desse modelo legado. O bootstrap atualizado já atende volumes novos.
O script preserva os dados, gera chaves para instituições existentes e interrompe a atualização
se encontrar IES sem funcionário. Identifique esses casos com:

```sql
SELECT i.id, i.nome FROM ies i
WHERE NOT EXISTS (
  SELECT 1 FROM usuario_ies v
  JOIN funcionario f ON f.id = v.usuario
  JOIN usuario u ON u.id = f.id AND u.tipo = 'funcionario'
  WHERE v.ies = i.id
);
```

Regularize os vínculos com os funcionários responsáveis antes de repetir o script.
Não confie no `ddl-auto=update` para gerar as chaves ou corrigir vínculos legados.
A criação livre de IES não verifica se o criador representa oficialmente a instituição.


### Testes do controle de IES

```bash
mvn compile
mvn -Dtest=JwtAuthenticationTest,IesServiceTest,FuncionarioCadastroTest,IesControllerTest,UsernameLoginTest,AuthControllerTest test
```

Os testes verificam autorização, chave privada, renovação, validação dos cadastros e contratos HTTP.
Usam mocks por subclasses para permitir execução em ambientes que restringem instrumentação da JVM.
A suíte acima não exige banco; rollback real, concorrência e migração precisam de validação com PostgreSQL.


## Login por username

`POST /auth/login` recebe exclusivamente o nome de usuário e a senha:

```json
{"username":"funcionario","senha":"exemplo"}
```

O username é obrigatório e único entre estudantes e funcionários, diferencia maiúsculas de
minúsculas e tem os espaços nas extremidades removidos no cadastro e no login.
O e-mail continua obrigatório no cadastro, mas não é usado para autenticar.
Os DTOs de autenticação documentam cada campo e sua obrigatoriedade no Swagger,
incluindo a escolha entre `novaIes` e o par `iesId`/`chaveIes`.

Em bancos existentes, execute `db/pessoais/migrations/0002-username-unico.sql`
antes de iniciar esta versão. O script interrompe a migração caso existam usernames
vazios ou duplicados após remover espaços externos; esses cadastros precisam ser
regularizados. Volumes novos já recebem a restrição no DDL.


### Migração do vínculo obrigatório do funcionário

Com a aplicação parada, aplique as migrações pendentes em ordem e execute
`db/pessoais/migrations/0003-funcionario-ies-obrigatoria.sql` como proprietário do schema
antes de iniciar esta versão. Não reaplique migrações já concluídas. O DDL de volumes novos
já contém `funcionario.ies_id NOT NULL`, sua FK e o índice.

A migração copia o vínculo único de cada funcionário de `usuario_ies` para a nova FK
antes de remover seus registros antigos da tabela intermediária. Os vínculos de estudantes
são preservados; `usuario_ies` deixa de ser fonte de autorização para funcionários.
Esta etapa não cria tabelas novas; a migração 0004 seguinte remove a tabela intermediária após migrar estudantes.
A migração interrompe sem alterar dados quando há funcionário sem IES, com várias IES,
ou IES sem funcionário. Identifique funcionários que precisam de regularização com:

```sql
SELECT f.id, array_agg(v.ies) FILTER (WHERE v.ies IS NOT NULL) AS instituicoes
FROM funcionario f LEFT JOIN usuario_ies v ON v.usuario = f.id
GROUP BY f.id HAVING count(v.ies) <> 1;
```

Defina os vínculos corretos antes de repetir a migração. O `ddl-auto=update` não faz essa
conversão. As regras de criação conjunta e de mínimo de um funcionário são garantidas
pelos fluxos transacionais da aplicação; a FK garante no banco que funcionário não fica sem IES.


### Migração do vínculo obrigatório do estudante

Após a migração 0003, com a aplicação parada, execute como proprietário do schema
`db/pessoais/migrations/0004-estudante-ies-obrigatoria.sql`. Ela move o vínculo único de
cada estudante de `usuario_ies` para `estudante.ies_id NOT NULL`, cria a FK e o índice,
e remove a tabela intermediária já sem uso. Não execute migrações legadas em volumes
novos criados com o DDL atual.

A migração interrompe sem confirmar alterações se um estudante tiver zero ou várias IES,
ou se ainda existirem vínculos de usuários que não sejam estudantes. Os responsáveis devem
regularizar esses casos antes de repetir o script; nenhuma IES é escolhida arbitrariamente.
As associações, entidades e repositório Java de `UsuarioIes` foram removidos. O DDL, os grants
e o script de remoção de tabelas foram ajustados ao modelo com duas FKs obrigatórias.

### Migração de CHAR para VARCHAR

Após a migração 0004, com a aplicação parada, execute como proprietário do schema
`db/pessoais/migrations/0005-char-para-varchar.sql` antes de iniciar esta versão.
Ela converte `ies.regiao_administrativa`, `usuario.username`, `estudante.matricula`
e `estudante.pais_origem` para `VARCHAR`, preservando os limites de 100, 50, 30 e 50
caracteres, respectivamente, além da unicidade e da obrigatoriedade existentes.

A conversão remove os espaços de preenchimento à direita dos valores `CHAR`, conforme
a [documentação do PostgreSQL](https://www.postgresql.org/docs/17/datatype-character.html).
O script executa as alterações em uma única transação. Não dependa de `ddl-auto=update`
para esta migração. Volumes novos já usam `VARCHAR` no DDL e não precisam executar o script.

## Auditoria de alterações

`auditoria.registro` registra cada `INSERT`, `UPDATE` e `DELETE` das tabelas `ies`,
`usuario`, `estudante` e `funcionario`, incluindo operações em cascata. O histórico
contém tabela, identificador do registro, operação, instante com fuso (`TIMESTAMPTZ`),
identificador de transação, origem, autor e valores anteriores e novos em JSONB.
O instante é o da execução do trigger, não o horário de commit. A exibição usa o
fuso da sessão do DBA.

A tabela e seus índices estão em `db/pessoais/DDL.sql`; a função e todos os triggers
estão em `db/pessoais/triggers.sql`. As permissões estão em `db/pessoais/ROLE.sql`.
Os campos são selecionados explicitamente nos triggers. Senha/hash, token do usuário
e chave de vínculo da IES nunca entram nos snapshots. Alterações apenas nesses campos
também geram eventos, mas não revelam seus valores. Novas tabelas precisam de triggers
na migração correspondente; novas colunas precisam passar pela revisão da lista de
campos permitidos antes de serem incluídas no histórico.

A aplicação informa somente a autoria, na mesma conexão e transação do Hibernate,
por `AuditoriaService`: usuário já autenticado e autorizado, ou `cadastro_publico`.
Ela não insere logs diretamente. Gravações feitas por `app_backend` sem contexto
válido falham. Novos fluxos de escrita devem identificar a autoria antes de persistir;
rotinas de cadastro aninhadas aproveitam o contexto da transação externa.
O contexto termina no commit ou rollback, sem permanecer na conexão reutilizada.
Esse comportamento de `set_config(..., true)` é definido na
[documentação do PostgreSQL](https://www.postgresql.org/docs/17/functions-admin.html).

SQL administrativo deve usar a conta individual do DBA: o log registra a conta
conectada ao banco como origem `banco`. A autoria da aplicação depende da autenticação
Java; quem tiver suas credenciais de banco poderá fornecer esse contexto e não deve
usá-las para administração. O histórico não é uma proteção contra um DBA/superusuário
que deliberadamente altere o banco ou desative os triggers. `TRUNCATE`, consultas,
mudanças de estrutura e tentativas revertidas não fazem parte deste histórico;
`app_backend` não tem permissão de `TRUNCATE`.

Não há endpoints de auditoria. A role da aplicação não pode consultar, inserir,
alterar ou excluir o histórico. O trigger usa uma função `SECURITY DEFINER`, com
`search_path` restrito e execução pública revogada, seguindo as
[orientações do PostgreSQL](https://www.postgresql.org/docs/17/sql-createfunction.html#SQL-CREATEFUNCTION-SECURITY).
Os objetos devem pertencer ao DBA, nunca à role da aplicação. Uma falha na auditoria
cancela a alteração de negócio; rollback também remove o log. A exclusão da conta
do autor não remove nem invalida sua identificação histórica.

### Aplicação em banco existente

Com a aplicação parada e as migrações anteriores já concluídas, execute **uma vez**
como DBA antes de iniciar a nova versão. Atualize o container PostgreSQL para montar
`db/pessoais` em `/opt/seed-db` (o volume de dados é preservado):

```bash
docker compose up -d postgres
docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f /opt/seed-db/migrations/0006-auditoria.sql'
```

A migração cria apenas a estrutura que falta no banco legado, aplica as restrições
de acesso e inclui `../triggers.sql` com `\ir`, tudo na mesma transação. Execute-a
por arquivo (`-f`), mantendo a estrutura de diretórios; não a envie pela entrada padrão.
Volumes novos executam somente `DDL.sql`, `ROLE.sql` e `triggers.sql`, nessa ordem.
Não execute a migração 0006 nesses volumes nem em bancos onde ela já foi aplicada.
A auditoria não cria histórico retroativo.
Não há limpeza automática; a retenção de dados pessoais deve ser definida antes
de produção. O script legado de remoção das tabelas de negócio preserva o schema
de auditoria; não o remova durante uma limpeza de dados da aplicação.

Como DBA, consulte, por exemplo:

```sql
SELECT ocorrido_em, tabela, registro_id, operacao, origem, usuario_id, username,
       conta_banco, valores_anteriores, valores_novos
FROM auditoria.registro
ORDER BY id DESC
LIMIT 100;
```

### Validação da auditoria

Em um PostgreSQL 17 **de testes**, com `DDL.sql`, `ROLE.sql` e `triggers.sql` aplicados
(ou atualizado pela migração 0006),
execute como superusuário:

```bash
psql -v ON_ERROR_STOP=1 -d seed_teste -f db/pessoais/tests/auditoria.sql
mvn compile
mvn -Dtest=JwtAuthenticationTest,IesServiceTest,FuncionarioCadastroTest,IesControllerTest,UsernameLoginTest,AuthControllerTest test
```

O SQL verifica cobertura das quatro tabelas, segredos omitidos, autoria, cascatas,
rollback, falha obrigatória sem autoria ou sem auditoria e negação das permissões
da aplicação. Os dados de teste são revertidos; as sequências podem avançar.
