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
    name = "formulario_base",
    indexes = @Index(name = "idx_formulario_base_id_estudante", columnList = "id_estudante"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class FormularioBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "id_estudante",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_formulario_base_estudante"))
  private Estudante estudante;

  @Column(name = "data_preenchimento", nullable = false)
  private LocalDate dataPreenchimento;

  @Column(columnDefinition = "text")
  private String anotacao;

  @Column(name = "secao_autoconhecimento", nullable = false)
  private boolean secaoAutoconhecimento;

  @Column(name = "secao_hidratacao", nullable = false)
  private boolean secaoHidratacao;

  @Column(name = "secao_exercicios", nullable = false)
  private boolean secaoExercicios;

  @Column(name = "secao_alimentacao", nullable = false)
  private boolean secaoAlimentacao;

  @Column(name = "secao_academia", nullable = false)
  private boolean secaoAcademia;

  @Column(name = "secao_mental", nullable = false)
  private boolean secaoMental;

  @Column(name = "secao_trabalho", nullable = false)
  private boolean secaoTrabalho;

  @Column(name = "secao_social", nullable = false)
  private boolean secaoSocial;

  @Column(name = "secao_sono", nullable = false)
  private boolean secaoSono;

  @Column(name = "secao_lazer", nullable = false)
  private boolean secaoLazer;

  @Column(name = "secao_cultura", nullable = false)
  private boolean secaoCultura;

  @Column(name = "secao_financeiro", nullable = false)
  private boolean secaoFinanceiro;

  @Column(name = "secao_amoroso", nullable = false)
  private boolean secaoAmoroso;

  @Column(name = "secao_acess", nullable = false)
  private boolean secaoAcess;
}
