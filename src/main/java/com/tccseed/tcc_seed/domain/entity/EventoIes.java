package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.entity.id.EventoIesId;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evento_ies")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class EventoIes {
    @EmbeddedId
    private EventoIesId id;
    @MapsId("idIes")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ies", nullable = false, foreignKey = @ForeignKey(name = "fk_evento_ies_ies"))
    private Ies ies;
    @MapsId("idEvento")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento", nullable = false, foreignKey = @ForeignKey(name = "fk_evento_ies_evento"))
    private Evento evento;
}
