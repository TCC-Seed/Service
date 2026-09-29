package com.tccseed.tcc_seed.controller.ies;

import com.tccseed.tcc_seed.controller.ies.dto.IesPrivadaResponse;
import com.tccseed.tcc_seed.controller.ies.dto.IesRequest;
import com.tccseed.tcc_seed.controller.ies.dto.IesResponse;
import com.tccseed.tcc_seed.service.ies.IesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/IES")
@RequiredArgsConstructor
public class IesController {
    private final IesService service;

    @Operation(summary = "Listar IES para seleção no cadastro",
            description = "Consulta pública, sem autenticação. Retorna uma página de instituições; "
                    + "cada item contém somente id, nome e regiaoAdministrativa. Não expõe a chave privada.")
    @GetMapping
    public Page<IesResponse> listar(@RequestParam(defaultValue = "0") int pagina,
                                    @RequestParam(defaultValue = "20") int tamanho) {
        return service.listar(pagina, tamanho);
    }

    @GetMapping("/{id}")
    public IesResponse consultar(@PathVariable Long id) {
        return service.consultar(id);
    }

    @GetMapping("/{id}/privado")
    public ResponseEntity<IesPrivadaResponse> consultarPrivada(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(service.consultarPrivada(id, principal.getName()));
    }

    @PutMapping("/{id}")
    public IesResponse atualizar(@PathVariable Long id, @Valid @RequestBody IesRequest request, Principal principal) {
        return service.atualizar(id, request, principal.getName());
    }

    @Operation(summary = "Excluir IES, estudantes e funcionários",
            description = "Somente funcionário da própria IES. Remove a instituição e as contas de todos os seus "
                    + "funcionários e estudantes, inclusive a do solicitante. "
                    + "Outros dados dependentes podem impedir a exclusão; nesse caso nenhuma remoção é confirmada.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, Principal principal) {
        service.excluir(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/chave/renovar")
    public ResponseEntity<IesPrivadaResponse> renovarChave(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(service.renovarChave(id, principal.getName()));
    }
}
