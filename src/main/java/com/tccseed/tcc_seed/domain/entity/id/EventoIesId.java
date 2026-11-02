package com.tccseed.tcc_seed.domain.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EventoIesId implements Serializable {
  @Column(name = "id_ies")
  private Long idIes;

  @Column(name = "id_evento")
  private Long idEvento;
}
