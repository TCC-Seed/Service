package com.tccseed.tcc_seed.domain.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CategoriaPublicacaoId implements Serializable {
    @Column(name = "id_publicacao")
    private Long idPublicacao;
    @Column(name = "categoria", length = 100)
    private String categoria;
}
