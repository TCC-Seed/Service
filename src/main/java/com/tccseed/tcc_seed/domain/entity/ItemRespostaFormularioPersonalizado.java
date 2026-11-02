package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.entity.id.ItemRespostaFormularioPersonalizadoId;
import jakarta.persistence.Column;
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
    name = "item_resposta_formulario_personalizado",
    indexes = @Index(name = "idx_item_resposta_id_pergunta", columnList = "id_pergunta"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemRespostaFormularioPersonalizado {
  @EmbeddedId private ItemRespostaFormularioPersonalizadoId id;

  @MapsId("idResposta")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_resposta",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_item_resposta_resposta"))
  private RespostaFormularioPersonalizado respostaFormulario;

  @MapsId("idPergunta")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_pergunta",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_item_resposta_pergunta"))
  private PerguntaFormularioPersonalizado pergunta;

  @Column(columnDefinition = "text")
  private String resposta;
}
