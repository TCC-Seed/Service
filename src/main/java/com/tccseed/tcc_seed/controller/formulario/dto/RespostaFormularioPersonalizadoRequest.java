package com.tccseed.tcc_seed.controller.formulario.dto;

import com.tccseed.tcc_seed.domain.entity.FormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.Usuario;
import java.time.LocalDate;

public record RespostaFormularioPersonalizadoRequest(Long idUsuario, Long idFormularioPersonalizado,
        LocalDate dataPreenchimento) {
    public RespostaFormularioPersonalizado toEntity(Usuario usuario) {
        RespostaFormularioPersonalizado entidade = new RespostaFormularioPersonalizado();
        FormularioPersonalizado formulario = new FormularioPersonalizado();
        formulario.setId(idFormularioPersonalizado);
        entidade.setUsuario(usuario);
        entidade.setFormulario(formulario);
        entidade.setDataPreenchimento(dataPreenchimento);
        return entidade;
    }
}
