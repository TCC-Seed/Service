package com.tccseed.tcc_seed.controller.formulario.dto;

import com.tccseed.tcc_seed.domain.entity.Estudante;
import com.tccseed.tcc_seed.domain.entity.FormularioBase;

import java.time.LocalDate;

public record FormularioBaseRequest(Long idEstudante, LocalDate dataPreenchimento, String anotacao,
                                    boolean secaoAutoconhecimento, boolean secaoHidratacao, boolean secaoExercicios,
                                    boolean secaoAlimentacao, boolean secaoAcademia, boolean secaoMental,
                                    boolean secaoTrabalho,
                                    boolean secaoSocial, boolean secaoSono, boolean secaoLazer, boolean secaoCultura,
                                    boolean secaoFinanceiro, boolean secaoAmoroso, boolean secaoAcess) {
    public FormularioBase toEntity() {
        FormularioBase entidade = new FormularioBase();
        Estudante estudante = new Estudante();
        estudante.setId(idEstudante);
        entidade.setEstudante(estudante);
        entidade.setDataPreenchimento(dataPreenchimento);
        entidade.setAnotacao(anotacao);
        entidade.setSecaoAutoconhecimento(secaoAutoconhecimento);
        entidade.setSecaoHidratacao(secaoHidratacao);
        entidade.setSecaoExercicios(secaoExercicios);
        entidade.setSecaoAlimentacao(secaoAlimentacao);
        entidade.setSecaoAcademia(secaoAcademia);
        entidade.setSecaoMental(secaoMental);
        entidade.setSecaoTrabalho(secaoTrabalho);
        entidade.setSecaoSocial(secaoSocial);
        entidade.setSecaoSono(secaoSono);
        entidade.setSecaoLazer(secaoLazer);
        entidade.setSecaoCultura(secaoCultura);
        entidade.setSecaoFinanceiro(secaoFinanceiro);
        entidade.setSecaoAmoroso(secaoAmoroso);
        entidade.setSecaoAcess(secaoAcess);
        return entidade;
    }
}
