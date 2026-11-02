package com.tccseed.tcc_seed.controller.formulariobase;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseAnotacaoResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseFuncionarioResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBasePageResponse;
import com.tccseed.tcc_seed.service.formulariobase.FormularioBaseCampo;
import com.tccseed.tcc_seed.service.formulariobase.FormularioBaseFuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/funcionario/formularios-base")
@RequiredArgsConstructor
@Tag(
    name = "formulario-base-funcionario",
    description =
        "Acesso pela sessão autenticada do funcionário; os resultados são limitados à IES dessa sessão.")
public class FormularioBaseFuncionarioController {
  private final FormularioBaseFuncionarioService service;

  @GetMapping("/respostas")
  @Operation(
      summary = "Listar respostas dos estudantes da IES do funcionário",
      description =
          "Retorna somente id, UUID pseudônimo do estudante, data e os booleanos selecionados. "
              + "Nenhum dado pessoal do estudante é incluído. Datas são inclusivas; desempates pelo id seguem a ordem escolhida.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de respostas"),
    @ApiResponse(
        responseCode = "400",
        description = "Filtro ou paginação inválidos",
        content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Sessão ausente ou inválida",
        content = @Content)
  })
  public FormularioBasePageResponse<FormularioBaseFuncionarioResponse> listarRespostas(
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
    return service.listarRespostas(
        token(authentication), dataInicio, dataFim, ordem, page, size, campos);
  }

  @GetMapping("/respostas/anotacoes")
  @Operation(
      summary = "Listar anotações das respostas dos estudantes da IES",
      description =
          "Usa os mesmos filtros inclusivos e paginação de /respostas, mas retorna anotação em vez de booleanos.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Página de anotações"),
    @ApiResponse(
        responseCode = "400",
        description = "Filtro ou paginação inválidos",
        content = @Content),
    @ApiResponse(
        responseCode = "401",
        description = "Sessão ausente ou inválida",
        content = @Content)
  })
  public FormularioBasePageResponse<FormularioBaseAnotacaoResponse> listarAnotacoes(
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
          int size) {
    return service.listarAnotacoes(token(authentication), dataInicio, dataFim, ordem, page, size);
  }

  private UUID token(Authentication authentication) {
    try {
      return UUID.fromString(authentication.getName());
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida.", exception);
    }
  }
}
