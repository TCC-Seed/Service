package com.tccseed.tcc_seed.controller.formulariobase;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseEstudanteResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBasePageResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseRequest;
import com.tccseed.tcc_seed.service.formulariobase.FormularioBaseCampo;
import com.tccseed.tcc_seed.service.formulariobase.FormularioBaseEstudanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/estudante/formularios-base")
@RequiredArgsConstructor
@Tag(
    name = "formulario-base-estudante",
    description =
        "Acesso pela sessão autenticada do estudante; operações são limitadas às respostas próprias.")
public class FormularioBaseEstudanteController {
  private final FormularioBaseEstudanteService service;

  @PostMapping("/respostas")
  @Operation(
      summary = "Preencher o formulário base",
      description =
          "O vínculo com o estudante e a data atual são definidos pelo servidor. Os 14 booleanos são obrigatórios; anotação é opcional.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content =
          @io.swagger.v3.oas.annotations.media.Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          "{\"secaoAutoconhecimento\":true,\"secaoHidratacao\":true,\"secaoExercicios\":false,\"secaoAlimentacao\":true,\"secaoAcademia\":false,\"secaoMental\":true,\"secaoTrabalho\":false,\"secaoSocial\":true,\"secaoSono\":false,\"secaoLazer\":true,\"secaoCultura\":false,\"secaoFinanceiro\":true,\"secaoAmoroso\":false,\"secaoAcess\":true,\"anotacao\":\"Texto livre\"}")))
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Resposta criada"),
    @ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Sessão ausente ou inválida",
        content = @Content)
  })
  public ResponseEntity<FormularioBaseEstudanteResponse> criar(
      @Valid @RequestBody FormularioBaseRequest request, Authentication authentication) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(service.criar(token(authentication), request));
  }

  @GetMapping("/respostas")
  @Operation(
      summary = "Listar minhas respostas",
      description =
          "Sempre inclui anotacao. Sem campos, retorna todos os booleanos; com campos, retorna somente os booleanos selecionados. Datas são inclusivas; desempates pelo id seguem a ordem escolhida.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página das respostas próprias"),
    @ApiResponse(
        responseCode = "400",
        description = "Filtro ou paginação inválidos",
        content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Sessão ausente ou inválida",
        content = @Content)
  })
  public FormularioBasePageResponse<FormularioBaseEstudanteResponse> listar(
      Authentication authentication,
      @Parameter(description = "Data inicial inclusiva (yyyy-MM-dd)", example = "2026-10-01")
          @RequestParam(name = "dataInicio", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataInicio,
      @Parameter(description = "Data final inclusiva (yyyy-MM-dd)", example = "2026-11-01")
          @RequestParam(name = "dataFim", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataFim,
      @Parameter(
              description = "Ordem por data e id",
              schema = @Schema(allowableValues = {"desc", "asc"}))
          @RequestParam(name = "ordem", defaultValue = "desc")
          String ordem,
      @Parameter(description = "Índice da página, começando em 0", example = "0")
          @RequestParam(name = "page", defaultValue = "0")
          int page,
      @Parameter(description = "Itens por página (1 a 100)", example = "20")
          @RequestParam(name = "size", defaultValue = "20")
          int size,
      @Parameter(
              description =
                  "Lista separada por vírgulas. Valores válidos: "
                      + FormularioBaseCampo.VALID_VALUES,
              example = "secaoSono,secaoHidratacao")
          @RequestParam(name = "campos", required = false)
          String campos) {
    return service.listar(token(authentication), dataInicio, dataFim, ordem, page, size, campos);
  }

  @GetMapping("/respostas/{id}")
  @Operation(
      summary = "Consultar uma resposta própria",
      description =
          "Retorna todos os booleanos e a anotação. Respostas de outro estudante retornam 404.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Resposta encontrada"),
    @ApiResponse(
        responseCode = "404",
        description = "Resposta inexistente ou de outro estudante",
        content = @Content)
  })
  public FormularioBaseEstudanteResponse buscar(
      @PathVariable Long id, Authentication authentication) {
    return service.buscar(token(authentication), id);
  }

  @PutMapping("/respostas/{id}")
  @Operation(
      summary = "Editar uma resposta própria",
      description =
          "Substitui os booleanos e a anotação. O estudante dono e a data de preenchimento não podem ser alterados.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Resposta atualizada"),
    @ApiResponse(responseCode = "400", description = "Campos inválidos", content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Resposta inexistente ou de outro estudante",
        content = @Content)
  })
  public FormularioBaseEstudanteResponse atualizar(
      @PathVariable Long id,
      @Valid @RequestBody FormularioBaseRequest request,
      Authentication authentication) {
    return service.atualizar(token(authentication), id, request);
  }

  @DeleteMapping("/respostas/{id}")
  @Operation(summary = "Excluir uma resposta própria")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Resposta excluída"),
    @ApiResponse(
        responseCode = "404",
        description = "Resposta inexistente ou de outro estudante",
        content = @Content)
  })
  public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication authentication) {
    service.excluir(token(authentication), id);
    return ResponseEntity.noContent().build();
  }

  private UUID token(Authentication authentication) {
    try {
      return UUID.fromString(authentication.getName());
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida.", exception);
    }
  }
}
