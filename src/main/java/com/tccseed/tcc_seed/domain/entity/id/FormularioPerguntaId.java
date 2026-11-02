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
public class FormularioPerguntaId implements Serializable {
  @Column(name = "id_formulario_personalizado")
  private Long idFormularioPersonalizado;

  @Column(name = "id_pergunta")
  private Long idPergunta;
}
