# TCC Seed — Service

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

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

Copie o arquivo de exemplo de variáveis de ambiente e ajuste os valores conforme necessário (principalmente o `JWT_SECRET`):

```bash
cp .env.example .env
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