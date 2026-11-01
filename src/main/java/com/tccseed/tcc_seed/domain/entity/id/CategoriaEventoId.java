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
public class CategoriaEventoId implements Serializable {
  @Column(name = "id_evento")
  private Long idEvento;

  @Column(name = "categoria", length = 100)
  private String categoria;
}
