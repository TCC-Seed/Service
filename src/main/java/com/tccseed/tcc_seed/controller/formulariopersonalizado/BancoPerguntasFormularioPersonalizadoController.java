package com.tccseed.tcc_seed.controller.formulariopersonalizado;

import com.tccseed.tcc_seed.controller.formulariopersonalizado.dto.PerguntaFormularioResponse;
import com.tccseed.tcc_seed.service.formulariopersonalizado.PerguntaFormularioPersonalizadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/funcionario/perguntas-formularios-personalizados")
@RequiredArgsConstructor
@Tag(name = "formulario-personalizado-funcionario")
public class BancoPerguntasFormularioPersonalizadoController {
  private final PerguntaFormularioPersonalizadoService service;

  @GetMapping
  @Operation(summary = "Pesquisar perguntas reutilizáveis")
  public Page<PerguntaFormularioResponse> listar(
      Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String busca) {
    return service.listar(page, size, busca, authentication.getName());
  }
}
