package com.tccseed.tcc_seed.controller.auth.dto;

import com.tccseed.tcc_seed.controller.ies.dto.IesRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioCadastroRequestTest {
    private FuncionarioCadastroRequest request(Long iesId, String chave, IesRequest novaIes) {
        return new FuncionarioCadastroRequest("pessoa@example.com", "pessoa", "senha", "Pessoa",
                "Psicologia", iesId, chave, novaIes);
    }

    @Test
    void vinculoExigeIesEChaveOuNovaIesExclusivamente() {
        assertTrue(request(1L, "chave", null).isVinculoValido());
        assertTrue(request(null, null, new IesRequest("Instituição", null)).isVinculoValido());
        assertFalse(request(null, "chave", null).isVinculoValido());
        assertFalse(request(1L, null, null).isVinculoValido());
        assertFalse(request(1L, "chave", new IesRequest("Instituição", null)).isVinculoValido());
        assertFalse(request(null, null, null).isVinculoValido());
    }
}
