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

O PostgreSQL executa `db/pessoais/DDL.sql` e `db/pessoais/ROLE.sql` automaticamente ao inicializar um volume de dados novo. Os scripts não são reaplicados a um volume já inicializado, para preservar os dados existentes.

### Inicializando um volume PostgreSQL existente

Se o volume `postgres-data` já existir sem o schema pessoal (tipos, domínio e tabelas), atualize primeiro o `.env` com uma senha em `SPRING_DATASOURCE_PASSWORD`. Depois execute o DDL e a role uma única vez:

```bash
docker compose up -d postgres
docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f /docker-entrypoint-initdb.d/01-DDL.sql'
docker compose exec -T postgres /docker-entrypoint-initdb.d/02-ROLE.sh
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

Todas as rotas `/IES` exigem o cookie de autenticação `seed_cookie` obtido no login.
Os dados institucionais são `id`, `nome` (obrigatório, até 200 caracteres) e
`regiaoAdministrativa` (opcional, até 100 caracteres). Nome não é identificador único.

| Método e rota | Permissão e resultado |
| --- | --- |
| `GET /IES?pagina=0&tamanho=20` | Usuário autenticado; lista paginada sem chave (máximo 100 por página) |
| `GET /IES/{id}` | Usuário autenticado; dados institucionais sem chave |
| `POST /IES` | Funcionário; cria e vincula o próprio solicitante; retorna 201 e Location |
| `PUT /IES/{id}` | Funcionário vinculado; substitui nome e região; retorna 200 |
| `DELETE /IES/{id}` | Funcionário vinculado; exclui IES e vínculos, preservando usuários; retorna 204 |
| `GET /IES/{id}/privado` | Funcionário vinculado; dados institucionais e `chaveIes`, sem cache |
| `POST /IES/{id}/vinculos` | Funcionário; exige `{"chaveIes":"..."}` válida para vincular o próprio solicitante; retorna 204 |
| `POST /IES/{id}/chave/renovar` | Funcionário vinculado; troca a chave e retorna os dados privados, sem cache |

Criação e atualização recebem `{"nome":"Universidade Exemplo","regiaoAdministrativa":"Brasília"}`.
A chave tem 256 bits aleatórios e nunca aparece nas respostas públicas, nem é recebida na URL.
A troca da chave impede novos ingressos com a chave antiga, sem remover funcionários já vinculados.
Não há criação de dashboard nesta entrega; futuros endpoints privados devem exigir o mesmo vínculo.

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
Funcionários já cadastrados podem manter vínculos com várias instituições.
Não há endpoint para desvincular o último funcionário ou deixar uma IES sem funcionário.

Erros: 400 para dados inválidos ou opções de cadastro incompatíveis; 401 sem autenticação;
403 para chave incorreta ou usuário sem permissão; 404 para IES inexistente;
409 quando dados dependentes impedem a exclusão. Estudantes, mesmo vinculados,
não podem acessar a chave ou alterar a IES.

### Atualização de um banco existente

Antes de iniciar esta versão, execute como proprietário do schema o script
`db/pessoais/migrations/0001-chave-ies.sql`. O bootstrap atualizado já atende volumes novos.
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
