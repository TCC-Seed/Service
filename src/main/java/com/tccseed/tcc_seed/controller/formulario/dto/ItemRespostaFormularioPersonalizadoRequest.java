package com.tccseed.tcc_seed.controller.formulario.dto;

import com.tccseed.tcc_seed.domain.entity.ItemRespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.RespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.ItemRespostaFormularioPersonalizadoId;

public record ItemRespostaFormularioPersonalizadoRequest(Long idResposta, Long idPergunta, String resposta) {
    public ItemRespostaFormularioPersonalizado toEntity() {
        ItemRespostaFormularioPersonalizado entidade = new ItemRespostaFormularioPersonalizado();
        ItemRespostaFormularioPersonalizadoId id = new ItemRespostaFormularioPersonalizadoId();
        id.setIdResposta(idResposta);
        id.setIdPergunta(idPergunta);
        entidade.setId(id);
        RespostaFormularioPersonalizado respostaRef = new RespostaFormularioPersonalizado();
        respostaRef.setId(idResposta);
        PerguntaFormularioPersonalizado pergunta = new PerguntaFormularioPersonalizado();
        pergunta.setId(idPergunta);
        entidade.setRespostaFormulario(respostaRef);
        entidade.setPergunta(pergunta);
        entidade.setResposta(resposta);
        return entidade;
    }
}
