# Inicializar o PostgreSQL apenas em volume novo

O Compose monta o DDL e a criação da role na pasta de inicialização da imagem oficial, que executa esses scripts somente quando o diretório de dados está vazio. Para preservar volumes existentes e evitar reaplicar DDL não idempotente, o volume atual recebe os scripts uma vez por comando documentado, sem ser removido.
