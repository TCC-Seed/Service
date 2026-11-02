package com.tccseed.tcc_seed.repository;

import java.time.LocalDate;
import java.util.UUID;

public interface FormularioBaseListProjection {
  Long getId();

  UUID getToken();

  LocalDate getDataPreenchimento();

  String getAnotacao();

  Boolean getSecaoAutoconhecimento();

  Boolean getSecaoHidratacao();

  Boolean getSecaoExercicios();

  Boolean getSecaoAlimentacao();

  Boolean getSecaoAcademia();

  Boolean getSecaoMental();

  Boolean getSecaoTrabalho();

  Boolean getSecaoSocial();

  Boolean getSecaoSono();

  Boolean getSecaoLazer();

  Boolean getSecaoCultura();

  Boolean getSecaoFinanceiro();

  Boolean getSecaoAmoroso();

  Boolean getSecaoAcess();
}
