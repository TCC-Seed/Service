package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.entity.id.UsuarioIesId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario_ies")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioIes {

    @EmbeddedId
    private UsuarioIesId id = new UsuarioIesId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ies")
    @JoinColumn(name = "ies", nullable = false)
    private Ies ies;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuario")
    @JoinColumn(name = "usuario", nullable = false)
    private Usuario usuario;
}
