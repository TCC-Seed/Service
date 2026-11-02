package com.tccseed.tcc_seed.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pergunta_formulario_personalizado")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PerguntaFormularioPersonalizado {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, columnDefinition = "text")
  private String enunciado;
}
