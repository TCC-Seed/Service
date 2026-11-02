package com.tccseed.tcc_seed.controller.formulario.dto;

import com.tccseed.tcc_seed.domain.entity.FormularioPergunta;
import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;

public record FormularioPerguntaRequest(Long idFormularioPersonalizado, Long idPergunta) {
  public FormularioPergunta toEntity() {
    FormularioPergunta entidade = new FormularioPergunta();
    FormularioPerguntaId id = new FormularioPerguntaId();
    id.setIdFormularioPersonalizado(idFormularioPersonalizado);
    id.setIdPergunta(idPergunta);
    entidade.setId(id);
    FormularioPersonalizado formulario = new FormularioPersonalizado();
    formulario.setId(idFormularioPersonalizado);
    PerguntaFormularioPersonalizado pergunta = new PerguntaFormularioPersonalizado();
    pergunta.setId(idPergunta);
    entidade.setFormulario(formulario);
    entidade.setPergunta(pergunta);
    return entidade;
  }
}
