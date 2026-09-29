# JWT em cookie para autenticação stateless

O login emite um JWT de um dia no cookie `seed_cookie`, assinado com `JWT_SECRET` e configurado como `HttpOnly`, `Secure` e `SameSite=Lax`, em vez de manter uma sessão no servidor. A role no token deriva do tipo de usuário, preservando a política stateless já configurada para a API.
