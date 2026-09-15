package com.tccseed.tcc_seed.domain.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioIesId implements Serializable {

    @Column(name = "ies")
    private Long ies;

    @Column(name = "usuario")
    private Long usuario;
}
