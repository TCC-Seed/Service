package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
public record RespostaRequest(@NotNull @Valid List<ItemRespostaRequest> respostas) {
  public RespostaRequest {
    respostas = respostas == null ? null : Collections.unmodifiableList(new ArrayList<>(respostas));
  }
}
