# Instruções do projeto

## Arquitetura de pacotes (regra fixa)

Todo código Java novo ou reorganizado deve seguir esta estrutura, sob
`src/main/java/com/tccseed/tcc_seed`:

```text
com.tccseed.tcc_seed
├── config/
├── security/
├── controller/<feature>/
│   └── dto/
├── service/<feature>/
├── domain/
│   ├── entity/
│   ├── enums/
│   └── converter/
├── repository/
├── exception/
└── TccSeedApplication
```

| Pacote | Responsabilidade e regra |
| --- | --- |
| `config/` | Somente classes `@Configuration` que registram beans, como `SecurityConfig` e `OpenApiConfig`. |
| `security/` | Lógica de segurança em execução, como filtros de autenticação e `JwtService`. |
| `controller/<feature>/` | Controllers REST da feature, como `controller/auth/AuthController`. |
| `controller/<feature>/dto/` | DTOs de requisição e resposta da mesma feature. |
| `service/<feature>/` | Lógica de negócio da feature, como `service/auth/AuthService`. |
| `domain/entity/` | Entidades persistidas e seus tipos auxiliares, incluindo identificadores compostos em `entity/id/`. |
| `domain/enums/` | Enumerações do domínio. |
| `domain/converter/` | Conversores de tipos do domínio para persistência. |
| `repository/` | Repositórios de acesso a dados. |
| `exception/` | Tratamento centralizado de erros (`ApiExceptionHandler`) e exceções customizadas. |
| Raiz `com.tccseed.tcc_seed` | Classe de inicialização `TccSeedApplication`. |

Use o mesmo nome de feature nos pacotes de controller e service (por exemplo,
`auth`). Não recrie `api/`, não coloque controllers ou services diretamente em
`controller/` ou `service/` e não coloque lógica de segurança em `config/`.
Uma classe `@Service` de segurança, como `JwtService`, pertence a `security/`.

Ao mover classes, ajuste as declarações `package`, todos os imports e demais
referências aos nomes anteriores. Preserve o comportamento da aplicação em
reorganizações de pacotes. Ao finalizar alterações Java, execute `mvn compile`
para verificar a compilação.
