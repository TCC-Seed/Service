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
    name = "publicacao",
    indexes = @Index(name = "idx_publicacao_id_funcionario", columnList = "id_funcionario"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Publicacao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_funcionario",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_publicacao_funcionario"))
  private Funcionario funcionario;

  @Column(nullable = false, length = 200)
  private String titulo;

  @Column(columnDefinition = "text")
  private String descricao;

  @Column(name = "data_publicacao", nullable = false)
  private LocalDate dataPublicacao;
}
