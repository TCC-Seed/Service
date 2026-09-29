# Aplicação conecta com role dedicada

O processo da aplicação conecta ao banco como `app_backend`, enquanto `POSTGRES_USER` fica reservado à administração e à inicialização do schema. A senha de `app_backend` vem de `SPRING_DATASOURCE_PASSWORD`, evitando que a aplicação use a role administrativa ou uma senha fixa no SQL.
