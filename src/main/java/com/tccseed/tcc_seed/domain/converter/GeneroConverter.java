// converter/GeneroConverter.java
package com.tccseed.tcc_seed.domain.converter;

import com.tccseed.tcc_seed.domain.enums.Genero;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GeneroConverter implements AttributeConverter<Genero, String> {

    @Override
    public String convertToDatabaseColumn(Genero attribute) {
        return attribute == null ? null : attribute.getValor();
    }

    @Override
    public Genero convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        for (Genero genero : Genero.values()) {
            if (genero.getValor().equals(dbData)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Valor desconhecido para GENERO: " + dbData);
    }
}