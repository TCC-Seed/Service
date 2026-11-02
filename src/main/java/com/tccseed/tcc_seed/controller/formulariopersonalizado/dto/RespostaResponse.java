package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

import java.time.LocalDate;
import java.util.List;

public record RespostaResponse(
    Long id,
    Long idUsuario,
    LocalDate dataPreenchimento,
    boolean preenchida,
    List<ItemRespostaResponse> itens) {}
