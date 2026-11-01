package com.tccseed.tcc_seed.domain.converter;

import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoUsuarioConverterTest {
    private final TipoUsuarioConverter converter = new TipoUsuarioConverter();

    @Test
    void converteTodosOsValoresDoDominio() {
        for (TipoUsuario tipo : TipoUsuario.values()) {
            assertEquals(tipo.getValor(), converter.convertToDatabaseColumn(tipo));
            assertEquals(tipo, converter.convertToEntityAttribute(tipo.getValor()));
        }
    }

    @Test
    void aceitaNulosERejeitaValorDesconhecido() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute("invalido"));
    }
}
