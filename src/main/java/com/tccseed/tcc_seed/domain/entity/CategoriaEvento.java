package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.entity.id.CategoriaEventoId;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria_evento")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CategoriaEvento {
    @EmbeddedId
    private CategoriaEventoId id;
    @MapsId("idEvento")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento", nullable = false, foreignKey = @ForeignKey(name = "fk_categoria_evento_evento"))
    private Evento evento;
    @Column(name = "categoria", nullable = false, length = 100, insertable = false, updatable = false)
    private String categoria;
}
