package com.tccseed.tcc_seed.controller.funcionario;

import com.tccseed.tcc_seed.controller.funcionario.dto.*;
import com.tccseed.tcc_seed.security.JwtAuthenticationFilter;
import com.tccseed.tcc_seed.service.funcionario.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/funcionario")
@RequiredArgsConstructor
public class FuncionarioController {
    private final FuncionarioService service;

    @GetMapping
    @Operation(summary = "Consultar os próprios dados", description = "Exclusivo do funcionário identificado pelo cookie seed_cookie. Não recebe ID de conta.")
    public ResponseEntity<FuncionarioResponse> consultar(Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.consultar(principal.getName()));
    }

    @PatchMapping
    @Operation(summary = "Editar os próprios dados", description = "Edição parcial de nome, formação, e-mail e username. IES é fixa. Campos ausentes ou nulos não são alterados.")
    public ResponseEntity<FuncionarioResponse> atualizar(Principal principal,
            @Valid @RequestBody FuncionarioAtualizacaoRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.atualizar(principal.getName(), request));
    }

    @PatchMapping("/senha")
    @Operation(summary = "Trocar a própria senha", description = "Exige a senha atual. Invalida todos os tokens anteriores e exige novo login.")
    public ResponseEntity<Void> alterarSenha(Principal principal,
            @Valid @RequestBody SenhaAtualizacaoRequest request) {
        service.alterarSenha(principal.getName(), request);
        return encerrarSessao();
    }

    @DeleteMapping
    @Operation(summary = "Excluir a própria conta", description = "Exige a senha atual. Exclui a própria conta. Se for o último funcionário, exclui também a IES e todos os estudantes e suas contas. Preserva a auditoria.")
    public ResponseEntity<Void> excluir(Principal principal,
            @Valid @RequestBody ContaExclusaoRequest request) {
        service.excluir(principal.getName(), request);
        return encerrarSessao();
    }

    private ResponseEntity<Void> encerrarSessao() {
        ResponseCookie cookie = ResponseCookie.from(JwtAuthenticationFilter.COOKIE_NAME, "")
                .httpOnly(true).secure(true).sameSite("Lax").path("/").maxAge(0).build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .cacheControl(CacheControl.noStore()).build();
    }
}
