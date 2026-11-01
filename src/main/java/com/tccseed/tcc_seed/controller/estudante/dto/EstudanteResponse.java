package com.tccseed.tcc_seed.controller.estudante.dto;

import com.tccseed.tcc_seed.domain.enums.Genero;
import java.time.LocalDate;

public record EstudanteResponse(Long id, String email, String username, String nome,
                                String matricula, LocalDate nascimento, Genero genero,
                                String paisOrigem, Long iesId) {
}
