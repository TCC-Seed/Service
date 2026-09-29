> Revisão: a ADR-0012 substitui a preservação das contas de estudantes na exclusão e remove a tabela intermediária após migrar seus vínculos.

# Uma IES obrigatória por funcionário

Cada funcionário pertence a exatamente uma IES, representada pela FK `funcionario.ies_id NOT NULL`, sem tabela intermediária para esse vínculo. A relação é 1:N: uma IES possui um ou mais funcionários. A FK é atribuída no cadastro e não é atualizada pela aplicação; não há transferência nesta etapa. Substitui as decisões correspondentes da ADR-0009.

A criação de IES ocorre exclusivamente no cadastro de seu primeiro funcionário, na mesma transação. `POST /IES` e `POST /IES/{id}/vinculos` são removidos. No cadastro, a IES é criada ou obtida com a chave correta antes de persistir o funcionário; qualquer falha desfaz a transação inteira. O cadastro em IES existente continua exigindo sua chave privada.

Por decisão explícita do produto, excluir a IES exclui todos os seus funcionários e respectivas contas, inclusive a do solicitante. A aplicação remove as entidades de funcionário para que a herança JOINED remova também os registros de usuário; uma cascata somente na FK da IES deixaria contas sem perfil. Estudantes têm apenas o vínculo com a IES removido. Referências de outros dados impedem a exclusão e fazem toda a transação ser revertida.

A migração não escolhe IES arbitrariamente: exige que cada funcionário legado possua exatamente um vínculo e que cada IES tenha funcionário. Transfere esses vínculos para a FK e remove apenas as associações antigas dos funcionários, preservando as de estudantes. A tabela usuario_ies permanece para vínculos estudantis e não concede permissões de funcionário.
