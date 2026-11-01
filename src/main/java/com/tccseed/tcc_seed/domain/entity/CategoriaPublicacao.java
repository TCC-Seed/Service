package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.entity.id.CategoriaPublicacaoId;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria_publicacao")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CategoriaPublicacao {
    @EmbeddedId
    private CategoriaPublicacaoId id;
    @MapsId("idPublicacao")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_publicacao", nullable = false, foreignKey = @ForeignKey(name = "fk_categoria_publicacao_publicacao"))
    private Publicacao publicacao;
    @Column(name = "categoria", nullable = false, length = 100, insertable = false, updatable = false)
    private String categoria;
}
