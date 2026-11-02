package com.tccseed.tcc_seed.controller.formulariopersonalizado;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResumoResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostaRequest;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostaResponse;
import com.tccseed.tcc_seed.service.formulariopersonalizado.FormularioPersonalizadoService;
import com.tccseed.tcc_seed.service.formulariopersonalizado.RespostaFormularioPersonalizadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estudante/formularios-personalizados")
@RequiredArgsConstructor
@Tag(name = "formulario-personalizado-estudante")
public class FormularioPersonalizadoEstudanteController {
  private final FormularioPersonalizadoService formularioService;
  private final RespostaFormularioPersonalizadoService respostaService;

  @GetMapping
  @Operation(summary = "Listar formulários disponíveis para o estudante")
  public List<FormularioResumoResponse> listar(Authentication authentication) {
    return formularioService.listarEstudante(authentication.getName());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consultar formulário e perguntas")
  public FormularioResponse buscar(@PathVariable Long id, Authentication authentication) {
    return formularioService.buscarEstudante(id, authentication.getName());
  }

  @PostMapping("/{id}/respostas")
  @Operation(summary = "Criar uma tentativa de resposta")
  public ResponseEntity<RespostaResponse> criar(
      @PathVariable Long id,
      @Valid @RequestBody RespostaRequest request,
      Authentication authentication) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(respostaService.criar(id, request, authentication.getName()));
  }

  @GetMapping("/{id}/respostas")
  @Operation(summary = "Listar minhas tentativas, da mais recente à mais antiga")
  public List<RespostaResponse> minhasRespostas(
      @PathVariable Long id, Authentication authentication) {
    return respostaService.minhasRespostas(id, authentication.getName());
  }

  @PutMapping("/{id}/respostas/{idResposta}")
  @Operation(summary = "Substituir os itens de uma tentativa minha")
  public RespostaResponse atualizar(
      @PathVariable Long id,
      @PathVariable Long idResposta,
      @Valid @RequestBody RespostaRequest request,
      Authentication authentication) {
    return respostaService.atualizarMinhaResposta(
        id, idResposta, request, authentication.getName());
  }

  @DeleteMapping("/{id}/respostas/{idResposta}")
  @Operation(summary = "Excluir uma tentativa minha")
  public ResponseEntity<Void> excluir(
      @PathVariable Long id, @PathVariable Long idResposta, Authentication authentication) {
    respostaService.excluirMinhaResposta(id, idResposta, authentication.getName());
    return ResponseEntity.noContent().build();
  }
}
