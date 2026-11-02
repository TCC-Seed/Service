package com.tccseed.tcc_seed.controller.formulariopersonalizado;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioRequest;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.FormularioResumoResponse;
import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.RespostasFuncionarioResponse;
import com.tccseed.tcc_seed.service.formulariopersonalizado.FormularioPersonalizadoService;
import com.tccseed.tcc_seed.service.formulariopersonalizado.RespostaFormularioPersonalizadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/funcionario/formularios-personalizados")
@RequiredArgsConstructor
@Tag(name = "formulario-personalizado-funcionario")
public class FormularioPersonalizadoFuncionarioController {
  private final FormularioPersonalizadoService formularioService;
  private final RespostaFormularioPersonalizadoService respostaService;

  @PostMapping
  @Operation(
      summary = "Criar formulário personalizado para a IES do funcionário",
      description =
          "Usa a autenticação do cookie de login, como os endpoints de eventos. Cada pergunta é identificada pelo enunciado e reutilizada ou criada automaticamente.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          "{\"titulo\":\"Pesquisa de satisfação\",\"perguntas\":[{\"enunciado\":\"Como avalia o curso?\"}]}")))
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Formulário criado"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
    @ApiResponse(responseCode = "401", description = "JWT ausente ou inválido", content = @Content)
  })
  public ResponseEntity<FormularioResponse> criar(
      @Valid @RequestBody FormularioRequest request, Authentication authentication) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(formularioService.criar(request, authentication.getName()));
  }

  @GetMapping
  @Operation(summary = "Listar formulários da IES do funcionário")
  public Page<FormularioResumoResponse> listar(
      Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return formularioService.listarFuncionario(authentication.getName(), page, size);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consultar formulário e perguntas")
  public FormularioResponse buscar(@PathVariable Long id, Authentication authentication) {
    return formularioService.buscarFuncionario(id, authentication.getName());
  }

  @PutMapping("/{id}")
  @Operation(summary = "Editar formulário personalizado")
  public FormularioResponse atualizar(
      @PathVariable Long id,
      @Valid @RequestBody FormularioRequest request,
      Authentication authentication) {
    return formularioService.atualizar(id, request, authentication.getName());
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir formulário personalizado")
  public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication authentication) {
    formularioService.excluir(id, authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/respostas")
  @Operation(summary = "Listar respostas dos estudantes da IES")
  public RespostasFuncionarioResponse respostas(
      @PathVariable Long id,
      Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return respostaService.respostasDoFormulario(id, authentication.getName(), page, size);
  }
}
