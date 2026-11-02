package com.tccseed.tcc_seed.controller.formulariopersonalizado.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RespostaRequest(@NotNull @Valid List<ItemRespostaRequest> respostas) {}
