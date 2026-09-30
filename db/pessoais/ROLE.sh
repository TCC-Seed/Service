#!/bin/sh
# --------------------------------------------------------------------------------------
# Data de Criação ........: 14/09/2026
# Autor(es) ..............: João Ginuino e Gabriela Lemos
# Versão .................: 1.0
# Descrição ..............: Execução do ROLE.sql com as variáveis do ambiente Docker.
# --------------------------------------------------------------------------------------
# Por que este arquivo existe:
# O ROLE.sql espera a variável psql app_backend_password. O PostgreSQL não substitui
# automaticamente as variáveis de ambiente do container dentro dos arquivos SQL.
# Este script lê SPRING_DATASOURCE_PASSWORD e a transmite ao psql, evitando gravar
# a senha diretamente no ROLE.sql. O SQL usa essa variável para configurar app_backend.
#
# Como é utilizado:
# O compose.yaml monta este arquivo em /docker-entrypoint-initdb.d/02-ROLE.sh.
# A imagem oficial do PostgreSQL o processa ao inicializar um volume novo, depois
# do 01-DDL.sql e antes do 03-triggers.sql. Em volumes existentes não há reaplicação
# automática; a execução manual requer que as estruturas esperadas pelo ROLE.sql
# já existam. O ROLE.sql é montado separadamente em /tmp/ROLE.sql, fora do diretório
# de inicialização, para que seja executado somente por este script, com a variável.
#
# Variáveis necessárias:
# SPRING_DATASOURCE_PASSWORD: senha da role app_backend, fornecida pelo ambiente.
# POSTGRES_USER: conta administrativa que executa a configuração de permissões.
# POSTGRES_DB: banco de dados que receberá essa configuração.
# Não habilitar set -x nem imprimir a senha nos logs.
#
# set -e interrompe em falhas; set -u rejeita variáveis não definidas.
set -eu

# Interrompe com uma mensagem clara se a senha estiver ausente ou vazia.
: "${SPRING_DATASOURCE_PASSWORD:?Defina SPRING_DATASOURCE_PASSWORD no arquivo .env}"

# ON_ERROR_STOP faz o psql retornar erro se qualquer comando SQL falhar.
# --set disponibiliza a senha como variável psql; --username e --dbname selecionam
# a conta administrativa e o banco. --file executa o SQL montado pelo Compose.
psql \
  --set=ON_ERROR_STOP=1 \
  --set=app_backend_password="$SPRING_DATASOURCE_PASSWORD" \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --file /tmp/ROLE.sql
