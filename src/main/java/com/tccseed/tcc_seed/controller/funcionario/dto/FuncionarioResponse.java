package com.tccseed.tcc_seed.controller.funcionario.dto;

public record FuncionarioResponse(Long id, String email, String username, String nome,
                                  String formacao, Long iesId) {
}
