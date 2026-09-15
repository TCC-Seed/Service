package com.tccseed.tcc_seed.domain.converter;

import com.tccseed.tcc_seed.domain.enums.TipoUsuario;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoUsuarioConverter implements AttributeConverter<TipoUsuario, String> {

    @Override
    public String convertToDatabaseColumn(TipoUsuario attribute) {
        return attribute == null ? null : attribute.getValor();
    }

    @Override
    public TipoUsuario convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.getValor().equals(dbData)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor desconhecido para TIPO_USUARIO: " + dbData);
    }
}