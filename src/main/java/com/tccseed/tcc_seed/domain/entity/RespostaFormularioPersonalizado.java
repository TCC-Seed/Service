package com.tccseed.tcc_seed.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "resposta_formulario_personalizado",
    indexes = {
      @Index(name = "idx_resposta_formulario_id_usuario", columnList = "id_usuario"),
      @Index(
          name = "idx_resposta_formulario_id_formulario",
          columnList = "id_formulario_personalizado")
    })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class RespostaFormularioPersonalizado {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_usuario",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_resposta_formulario_usuario"))
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_formulario_personalizado",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_resposta_formulario_formulario"))
  private FormularioPersonalizado formulario;

  @Column(name = "data_preenchimento", nullable = false)
  private LocalDate dataPreenchimento;
}
