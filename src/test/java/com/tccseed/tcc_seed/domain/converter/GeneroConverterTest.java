package com.tccseed.tcc_seed.domain.converter;

import com.tccseed.tcc_seed.domain.enums.Genero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneroConverterTest {
    private final GeneroConverter converter = new GeneroConverter();

    @Test
    void converteTodosOsValoresDoDominio() {
        for (Genero genero : Genero.values()) {
            assertEquals(genero.getValor(), converter.convertToDatabaseColumn(genero));
            assertEquals(genero, converter.convertToEntityAttribute(genero.getValor()));
        }
    }

    @Test
    void aceitaNulosERejeitaValorDesconhecido() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute("invalido"));
    }
}
