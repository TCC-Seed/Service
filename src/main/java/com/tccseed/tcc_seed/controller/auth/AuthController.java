package com.tccseed.tcc_seed.controller.auth;

import com.tccseed.tcc_seed.controller.auth.dto.EstudanteCadastroRequest;
import com.tccseed.tcc_seed.controller.auth.dto.FuncionarioCadastroRequest;
import com.tccseed.tcc_seed.controller.auth.dto.LoginRequest;
import com.tccseed.tcc_seed.controller.auth.dto.UsuarioResponse;
import com.tccseed.tcc_seed.security.JwtAuthenticationFilter;
import com.tccseed.tcc_seed.security.JwtService;
import com.tccseed.tcc_seed.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.autenticar(request);
        ResponseCookie cookie = ResponseCookie.from(JwtAuthenticationFilter.COOKIE_NAME, result.jwt())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(JwtService.TOKEN_LIFETIME)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.usuario());
    }

    @Operation(summary = "Cadastrar estudante",
            description = "Cadastro público: qualquer pessoa pode chamar, sem autenticação ou role. "
                    + "Cria um estudante vinculado a exatamente uma IES existente, selecionada pelo iesId obrigatório. "
                    + "Consulte GET /IES sem login para obter id, nome e região administrativa. "
                    + "Não exige a chave privada da IES nem permite criar uma instituição neste cadastro.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estudante cadastrado e vinculado à IES.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Corpo inválido ou campos obrigatórios ausentes.", content = @Content),
            @ApiResponse(responseCode = "404", description = "IES informada não encontrada.", content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail, username ou matrícula já cadastrados.", content = @Content)
    })
    @PostMapping("/cadastro/estudante")
    public ResponseEntity<UsuarioResponse> cadastrarEstudante(
            @Valid @RequestBody EstudanteCadastroRequest request) {
        return ResponseEntity.status(201).body(authService.cadastrarEstudante(request));
    }

    @Operation(summary = "Cadastrar funcionário",
            description = "Cadastro público: qualquer pessoa pode chamar, sem autenticação ou role. "
                    + "Informe novaIes para criar uma instituição, ou iesId e chaveIes para entrar em uma existente. "
                    + "Cada funcionário pertence a exatamente uma IES, escolhida no cadastro. As opções são mutuamente exclusivas. "
                    + "A chave deve ser fornecida por um funcionário vinculado à IES.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Funcionário cadastrado e vinculado à IES.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Corpo inválido, campos ausentes ou opções de vínculo incompatíveis.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Chave da IES inválida.", content = @Content),
            @ApiResponse(responseCode = "404", description = "IES informada não encontrada.", content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail ou username já cadastrados.", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = FuncionarioCadastroRequest.class),
            examples = {
                    @ExampleObject(name = "Criar nova IES", value = """
                            {"email":"ana.silva@example.com","username":"ana.silva","senha":"Exemplo@2026",
                             "nome":"Ana Silva","formacao":"Psicologia",
                             "novaIes":{"nome":"Universidade Exemplo","regiaoAdministrativa":"Brasília"}}
                            """),
                    @ExampleObject(name = "Entrar em IES existente", value = """
                            {"email":"ana.silva@example.com","username":"ana.silva","senha":"Exemplo@2026",
                             "nome":"Ana Silva","formacao":"Psicologia","iesId":1,
                             "chaveIes":"exemplo_de_chave_privada_compartilhada_da_ies"}
                            """)
            }))
    @PostMapping("/cadastro/funcionario")
    public ResponseEntity<UsuarioResponse> cadastrarFuncionario(
            @Valid @RequestBody FuncionarioCadastroRequest request) {
        return ResponseEntity.status(201).body(authService.cadastrarFuncionario(request));
    }
}
