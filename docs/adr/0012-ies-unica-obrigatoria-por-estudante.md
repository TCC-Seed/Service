# Uma IES obrigatória por estudante

Cada estudante pertence a exatamente uma IES existente, representada por `estudante.ies_id NOT NULL`. O cadastro público exige somente o ID da instituição, sem a chave privada reservada ao ingresso de funcionários. A escolha ocorre no cadastro; não há criação de IES nem transferência pelo estudante. O login permanece por username e senha.

`GET /IES` passa a ser público e retorna uma página com id, nome e região administrativa, permitindo escolher a instituição antes do cadastro. As demais rotas continuam autenticadas e os dados privados/alterações exigem funcionário da própria instituição. A FK estudantil não concede permissões administrativas.

Por decisão explícita do produto, a exclusão de IES remove também todos os estudantes e suas contas, além dos funcionários e respectivas contas. Esta decisão substitui a preservação de estudantes descrita na ADR-0011. A operação é transacional; outras dependências que impeçam a remoção fazem a exclusão inteira ser revertida.

O cadastro estudantil bloqueia a IES enquanto salva o usuário, serializando-o com a exclusão. A migração exige exatamente um vínculo legado por estudante, preserva-o na FK e remove usuario_ies. Casos sem vínculo, com múltiplos vínculos ou vínculos inesperados interrompem a migração sem escolher uma IES arbitrariamente.
