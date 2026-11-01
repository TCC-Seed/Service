package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.entity.id.FormularioPerguntaId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "formulario_pergunta",
    indexes = @Index(name = "idx_formulario_pergunta_id_pergunta", columnList = "id_pergunta"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class FormularioPergunta {
  @EmbeddedId private FormularioPerguntaId id;

  @MapsId("idFormularioPersonalizado")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_formulario_personalizado",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_formulario_pergunta_formulario"))
  private FormularioPersonalizado formulario;

  @MapsId("idPergunta")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_pergunta",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_formulario_pergunta_pergunta"))
  private PerguntaFormularioPersonalizado pergunta;
}
