# Seed — pessoas e instituições

Este contexto define as pessoas reconhecidas pelo Seed e sua relação com instituições de ensino superior.

## Pessoas e perfis

**Usuário**:
Pessoa cadastrada no Seed como estudante ou funcionário. Seu nome de usuário (username) é o identificador único de login; o e-mail é um dado do cadastro.
_Evitar_: conta genérica, perfil genérico

**Estudante**:
Usuário cujo tipo é estudante, possui dados acadêmicos próprios, incluindo matrícula e dados pessoais, e pertence a exatamente uma IES.
_Evitar_: aluno, usuário estudante

**Funcionário**:
Usuário cujo tipo é funcionário, que possui dados profissionais próprios, incluindo nome e formação, e trabalha em exatamente uma IES.
_Evitar_: colaborador, usuário funcionário

**Tipo de usuário**:
Classificação do usuário como estudante ou funcionário.
_Evitar_: tipo de conta, role

**Role de acesso**:
Categoria de autorização associada ao tipo de usuário: estudante ou funcionário.
_Evitar_: papel, tipo de usuário

## Instituições e vínculos

**IES**:
Instituição de ensino superior registrada no Seed, identificada por nome e, quando informado, região administrativa, e que possui pelo menos um funcionário vinculado.
_Evitar_: faculdade, campus

**Vínculo com IES**:
Relação entre um usuário e sua única IES. Uma IES possui um ou mais funcionários e pode possuir vários estudantes; cada estudante e cada funcionário pertencem a exatamente uma instituição.
_Evitar_: usuário da instituição, matrícula institucional

**Funcionário vinculado à IES**:
Funcionário que trabalha em sua única IES e está autorizado a administrar os dados dessa instituição.
_Evitar_: funcionário de qualquer IES, administrador global

**Chave de vínculo da IES**:
Credencial privada de uma IES, acessível apenas aos seus funcionários vinculados e compartilhada para permitir a entrada de novos funcionários nessa instituição.
_Evitar_: senha do funcionário, identificador público da IES

## Dados acadêmicos e pessoais

**Matrícula**:
Identificador acadêmico único de um estudante.
_Evitar_: número de usuário

**Gênero**:
Categoria informada para um estudante: feminino, masculino, não informar ou outro.
_Evitar_: sexo

## Histórico de alterações

**Registro de auditoria do Seed**:
Registro de uma criação, alteração ou exclusão de dados do Seed, com autoria, data e hora e valores anteriores e novos, sem credenciais de acesso.
_Evitar_: registro de autenticação, histórico de login
