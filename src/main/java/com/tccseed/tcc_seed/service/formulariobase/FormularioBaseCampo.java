package com.tccseed.tcc_seed.service.formulariobase;

import com.tccseed.tcc_seed.controller.formulariobase.dto.FormularioBaseBooleanosResponse;
import com.tccseed.tcc_seed.domain.entity.FormularioBase;
import com.tccseed.tcc_seed.repository.FormularioBaseListProjection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum FormularioBaseCampo {
  SECAO_AUTOCONHECIMENTO(
      "secaoAutoconhecimento",
      FormularioBaseListProjection::getSecaoAutoconhecimento,
      FormularioBaseBooleanosResponse::setSecaoAutoconhecimento,
      FormularioBase::isSecaoAutoconhecimento),
  SECAO_HIDRATACAO(
      "secaoHidratacao",
      FormularioBaseListProjection::getSecaoHidratacao,
      FormularioBaseBooleanosResponse::setSecaoHidratacao,
      FormularioBase::isSecaoHidratacao),
  SECAO_EXERCICIOS(
      "secaoExercicios",
      FormularioBaseListProjection::getSecaoExercicios,
      FormularioBaseBooleanosResponse::setSecaoExercicios,
      FormularioBase::isSecaoExercicios),
  SECAO_ALIMENTACAO(
      "secaoAlimentacao",
      FormularioBaseListProjection::getSecaoAlimentacao,
      FormularioBaseBooleanosResponse::setSecaoAlimentacao,
      FormularioBase::isSecaoAlimentacao),
  SECAO_ACADEMIA(
      "secaoAcademia",
      FormularioBaseListProjection::getSecaoAcademia,
      FormularioBaseBooleanosResponse::setSecaoAcademia,
      FormularioBase::isSecaoAcademia),
  SECAO_MENTAL(
      "secaoMental",
      FormularioBaseListProjection::getSecaoMental,
      FormularioBaseBooleanosResponse::setSecaoMental,
      FormularioBase::isSecaoMental),
  SECAO_TRABALHO(
      "secaoTrabalho",
      FormularioBaseListProjection::getSecaoTrabalho,
      FormularioBaseBooleanosResponse::setSecaoTrabalho,
      FormularioBase::isSecaoTrabalho),
  SECAO_SOCIAL(
      "secaoSocial",
      FormularioBaseListProjection::getSecaoSocial,
      FormularioBaseBooleanosResponse::setSecaoSocial,
      FormularioBase::isSecaoSocial),
  SECAO_SONO(
      "secaoSono",
      FormularioBaseListProjection::getSecaoSono,
      FormularioBaseBooleanosResponse::setSecaoSono,
      FormularioBase::isSecaoSono),
  SECAO_LAZER(
      "secaoLazer",
      FormularioBaseListProjection::getSecaoLazer,
      FormularioBaseBooleanosResponse::setSecaoLazer,
      FormularioBase::isSecaoLazer),
  SECAO_CULTURA(
      "secaoCultura",
      FormularioBaseListProjection::getSecaoCultura,
      FormularioBaseBooleanosResponse::setSecaoCultura,
      FormularioBase::isSecaoCultura),
  SECAO_FINANCEIRO(
      "secaoFinanceiro",
      FormularioBaseListProjection::getSecaoFinanceiro,
      FormularioBaseBooleanosResponse::setSecaoFinanceiro,
      FormularioBase::isSecaoFinanceiro),
  SECAO_AMOROSO(
      "secaoAmoroso",
      FormularioBaseListProjection::getSecaoAmoroso,
      FormularioBaseBooleanosResponse::setSecaoAmoroso,
      FormularioBase::isSecaoAmoroso),
  SECAO_ACESS(
      "secaoAcess",
      FormularioBaseListProjection::getSecaoAcess,
      FormularioBaseBooleanosResponse::setSecaoAcess,
      FormularioBase::isSecaoAcess);

  public static final String VALID_VALUES =
      "secaoAutoconhecimento, secaoHidratacao, secaoExercicios, secaoAlimentacao, "
          + "secaoAcademia, secaoMental, secaoTrabalho, secaoSocial, secaoSono, secaoLazer, "
          + "secaoCultura, secaoFinanceiro, secaoAmoroso, secaoAcess";

  private final String nome;
  private final Function<FormularioBaseListProjection, Boolean> projetado;
  private final BiConsumer<FormularioBaseBooleanosResponse, Boolean> setter;
  private final Function<FormularioBase, Boolean> entidade;

  FormularioBaseCampo(
      String nome,
      Function<FormularioBaseListProjection, Boolean> projetado,
      BiConsumer<FormularioBaseBooleanosResponse, Boolean> setter,
      Function<FormularioBase, Boolean> entidade) {
    this.nome = nome;
    this.projetado = projetado;
    this.setter = setter;
    this.entidade = entidade;
  }

  public String nome() {
    return nome;
  }

  public Boolean valor(FormularioBaseListProjection projection) {
    return projetado.apply(projection);
  }

  public Boolean valor(FormularioBase formularioBase) {
    return entidade.apply(formularioBase);
  }

  public void incluir(FormularioBaseBooleanosResponse response, Boolean valor) {
    setter.accept(response, valor);
  }

  public static List<FormularioBaseCampo> todos() {
    return List.of(values());
  }

  public static List<FormularioBaseCampo> parse(String campos) {
    if (campos == null || campos.isBlank()) return todos();
    List<String> nomes = Arrays.stream(campos.split(",", -1)).map(String::trim).toList();
    List<String> invalidos =
        nomes.stream().filter(nome -> localizar(nome) == null).distinct().toList();
    if (!invalidos.isEmpty()) {
      throw new IllegalArgumentException(
          "Campos inválidos: " + invalidos + ". Valores válidos: " + nomesValidos() + ".");
    }
    return nomes.stream().map(FormularioBaseCampo::localizar).distinct().toList();
  }

  public static String nomesValidos() {
    return Arrays.stream(values())
        .map(FormularioBaseCampo::nome)
        .reduce((a, b) -> a + ", " + b)
        .orElse("");
  }

  private static FormularioBaseCampo localizar(String nome) {
    String normalizado = nome.toLowerCase(Locale.ROOT);
    return Arrays.stream(values())
        .filter(campo -> campo.nome.toLowerCase(Locale.ROOT).equals(normalizado))
        .findFirst()
        .orElse(null);
  }
}
