# Usar Usuario existente com cadastros por tipo

As credenciais pertencem à hierarquia `Usuario`, que já possui os subtipos `Estudante` e `Funcionario`, mas não há um usuário concreto sem perfil. O cadastro cria o subtipo correspondente em endpoints separados, em vez de introduzir uma identidade genérica, para preservar os campos obrigatórios e o tipo de usuário existente.
