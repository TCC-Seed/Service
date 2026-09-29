> Revisão: a ADR-0011 substitui as decisões de criação direta, múltiplas IES por funcionário e preservação de funcionários na exclusão. As regras de chave privada permanecem válidas.

# Vínculo de funcionário e chave privada da IES

Uma IES nasce junto com o vínculo do primeiro funcionário, na mesma transação. O cadastro público de funcionário exige criar uma IES ou apresentar a chave de uma IES existente; funcionários autenticados também podem criar instituições e se vincular a várias IES. Consultas institucionais exigem autenticação; dados privados e alterações exigem que o usuário seja funcionário vinculado à instituição consultada, independentemente da role declarada no JWT.

A chave de vínculo é um segredo aleatório recuperável, pois os funcionários precisam consultá-la e compartilhá-la. Um hash unidirecional de senha não atenderia a essa necessidade. Qualquer funcionário vinculado pode trocar a chave, sem revogar vínculos existentes. A criação permanece livre: a chave controla a entrada em uma instituição cadastrada, mas não comprova a legitimidade de seu criador.

A exclusão remove a IES e seus vínculos na mesma transação, preserva os usuários e é bloqueada por referências de outros dados. Não há operação de remoção isolada de vínculo nesta etapa, evitando remover o último funcionário. As operações de escrita sobre uma IES existente usam bloqueio da instituição para serializar ingresso, alteração, troca de chave e exclusão. O mínimo de funcionários é garantido pelos fluxos transacionais da aplicação; alterações diretas no banco devem preservar a mesma regra.
