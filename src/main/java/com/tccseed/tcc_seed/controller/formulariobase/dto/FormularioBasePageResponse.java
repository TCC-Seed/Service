package com.tccseed.tcc_seed.controller.formulariobase.dto;

import java.util.List;

@io.swagger.v3.oas.annotations.media.Schema(
    example = "{\"content\":[],\"page\":0,\"size\":20,\"totalElements\":0,\"totalPages\":0}")
public record FormularioBasePageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {}
