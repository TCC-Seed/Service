package com.tccseed.tcc_seed.domain.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Evento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "data_inicio", nullable = false)
  private LocalDate dataInicio;

  @Column(name = "data_fim")
  private LocalDate dataFim;

  @Column(length = 500)
  private String link;

  @Column(columnDefinition = "text")
  private String descricao;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "categoria_evento",
      joinColumns =
          @JoinColumn(
              name = "id_evento",
              foreignKey = @ForeignKey(name = "fk_categoria_evento_evento")))
  @Column(name = "categoria", nullable = false, length = 100)
  private Set<String> categorias = new LinkedHashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "evento_ies",
      joinColumns =
          @JoinColumn(name = "id_evento", foreignKey = @ForeignKey(name = "fk_evento_ies_evento")),
      inverseJoinColumns =
          @JoinColumn(name = "id_ies", foreignKey = @ForeignKey(name = "fk_evento_ies_ies")))
  private Set<Ies> ies = new LinkedHashSet<>();
}
