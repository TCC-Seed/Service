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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "formulario_personalizado",
    indexes = @Index(name = "idx_formulario_personalizado_ies", columnList = "id_ies"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class FormularioPersonalizado {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String titulo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_ies",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_formulario_personalizado_ies"))
  private Ies ies;

  @Column(name = "criado_em", nullable = false)
  private LocalDateTime criadoEm;

  @OneToMany(mappedBy = "formulario", fetch = FetchType.LAZY)
  private List<FormularioPergunta> vinculos = new ArrayList<>();
}
