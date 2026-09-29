# Username como identificador de login

Por decisão do produto, o login passa a receber exclusivamente username e senha, substituindo a ADR-0002. O e-mail continua obrigatório e único no cadastro, mas não é consultado na autenticação. O username é único entre todos os usuários; cadastro e login removem espaços nas extremidades e preservam a distinção entre maiúsculas e minúsculas. A migração exige regularizar usernames vazios ou duplicados antes de ativar a unicidade, sem renomear pessoas arbitrariamente.
