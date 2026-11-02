package com.tccseed.tcc_seed.service.formulariobase;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseAnotacaoResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseEstudanteResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseFuncionarioResponse;
import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseRequest;
import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import com.tccseed.tcc_seed.repository.FormularioBaseListProjection;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FormularioBaseResponseMapper {

  public FormularioBaseFuncionarioResponse funcionarioBooleanos(
      FormularioBaseListProjection projection, List<FormularioBaseCampo> campos) {
    FormularioBaseFuncionarioResponse response =
        new FormularioBaseFuncionarioResponse(
            projection.getId(), projection.getToken(), projection.getDataPreenchimento());
    campos.forEach(campo -> campo.incluir(response, campo.valor(projection)));
    return response;
  }

  public FormularioBaseAnotacaoResponse funcionarioAnotacao(
      FormularioBaseListProjection projection) {
    return new FormularioBaseAnotacaoResponse(
        projection.getId(),
        projection.getToken(),
        projection.getDataPreenchimento(),
        projection.getAnotacao());
  }

  public FormularioBaseEstudanteResponse estudante(
      FormularioBaseListProjection projection, List<FormularioBaseCampo> campos) {
    FormularioBaseEstudanteResponse response =
        new FormularioBaseEstudanteResponse(
            projection.getId(), projection.getDataPreenchimento(), projection.getAnotacao());
    campos.forEach(campo -> campo.incluir(response, campo.valor(projection)));
    return response;
  }

  public FormularioBaseEstudanteResponse estudanteDetalhe(FormularioBase formulario) {
    FormularioBaseEstudanteResponse response =
        new FormularioBaseEstudanteResponse(
            formulario.getId(), formulario.getDataPreenchimento(), formulario.getAnotacao());
    FormularioBaseCampo.todos().forEach(campo -> campo.incluir(response, campo.valor(formulario)));
    return response;
  }

  public void atualizar(FormularioBase formulario, FormularioBaseRequest request) {
    formulario.setAnotacao(request.anotacao());
    formulario.setSecaoAutoconhecimento(request.secaoAutoconhecimento());
    formulario.setSecaoHidratacao(request.secaoHidratacao());
    formulario.setSecaoExercicios(request.secaoExercicios());
    formulario.setSecaoAlimentacao(request.secaoAlimentacao());
    formulario.setSecaoAcademia(request.secaoAcademia());
    formulario.setSecaoMental(request.secaoMental());
    formulario.setSecaoTrabalho(request.secaoTrabalho());
    formulario.setSecaoSocial(request.secaoSocial());
    formulario.setSecaoSono(request.secaoSono());
    formulario.setSecaoLazer(request.secaoLazer());
    formulario.setSecaoCultura(request.secaoCultura());
    formulario.setSecaoFinanceiro(request.secaoFinanceiro());
    formulario.setSecaoAmoroso(request.secaoAmoroso());
    formulario.setSecaoAcess(request.secaoAcess());
  }
}
