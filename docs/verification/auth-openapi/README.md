# Verificação da documentação de /auth

## Diagnóstico antes das alterações

O GET HTTP em `http://localhost:8080/v3/api-docs` retornou 200. O arquivo
`before.json` contém um recorte dessa resposta: rotas de /auth e schemas usados por elas.
Todos os campos de EstudanteCadastroRequest já tinham description. O array required
continha email, matricula, nascimento, nome, paisOrigem, senha e username;
senha já tinha writeOnly=true. Os demais DTOs de /auth também tinham descrições.
A alegada ausência de descrições não foi reproduzida no JSON. Não é possível atribuí-la
à interface, cache ou outra instância sem evidência adicional. Example Value exibe
exemplos de payload; as descrições ficam na aba Schema.

O projeto usa Spring Boot 4.1.1 e springdoc-openapi-starter-webmvc-ui 3.1.0.
A matriz oficial associa springdoc 3.x ao Boot 4 e springdoc 2.x ao Boot 3:
https://springdoc.org/ . Nenhuma dependência foi alterada.
Os imports são io.swagger.v3.oas.annotations.media.Schema e o AuthController usa
exatamente os records documentados em @RequestBody e UsuarioResponse no retorno.
Não há propriedades springdoc.*, ModelResolver ou OperationCustomizer personalizados,
configuração de naming strategy ou mixins. OpenApiConfig configura apenas Info.
SecurityFilterChain libera /auth/**, e não há @PreAuthorize nos endpoints.

A divergência efetivamente encontrada era o status de sucesso: o springdoc inferia
200 a partir de ResponseEntity<UsuarioResponse>, enquanto o controller retorna 201.
ApiResponses agora declara 201 explicitamente, preservando a execução real.
Os erros 400 e 409 foram documentados nos dois cadastros; funcionário também documenta
403 para chave inválida e 404 para IES inexistente.

## Alterações e resultado

- Os quatro DTOs de /auth receberam exemplos nos campos, preservando validações e descrições.
- AuthController recebeu Operation, ApiResponses e dois exemplos alternativos de cadastro
  de funcionário, evitando combinar novaIes com iesId/chaveIes no mesmo exemplo.
- AuthOpenApiTest faz GET /v3/api-docs via MockMvc com springdoc, AuthController,
  OpenApiConfig e SecurityConfig reais; o serviço e as dependências de autenticação são mocks.
  O teste verifica descrições, exemplos, required, writeOnly, referências dos DTOs e respostas.

`after.json` é um recorte do JSON desse GET em contexto de teste. Não é uma captura do
processo em localhost:8080. A comparação confirmou que descrições e restrições dos
campos permaneceram iguais; apenas os exemplos foram acrescentados aos schemas.

```bash
mvn compile
mvn -Dtest=JwtAuthenticationTest,IesServiceTest,FuncionarioCadastroTest,IesControllerTest,UsernameLoginTest,AuthControllerTest test
mvn -Dtest=AuthOpenApiTest test
```

Compilação aprovada; 34 testes anteriores e 1 teste do endpoint OpenAPI passaram.
Não foi executado o contextLoads que depende do banco.

## Limitação de ambiente

O segundo GET HTTP em localhost:8080 ainda retornou o JSON anterior. A consulta ao Docker
para identificar e atualizar o container foi recusada pelo usuário. Portanto, não houve
reinício do processo nem confirmação das melhorias no servidor já em execução.
O diagnóstico inicial usou a aplicação real; a verificação das alterações usou o contexto
Spring Boot de teste, sem banco. A interface visual do Swagger não foi inspecionada.
