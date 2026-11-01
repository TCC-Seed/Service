package com.tccseed.tcc_seed.domain.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "publicacao", indexes = @Index(name = "idx_publicacao_id_funcionario", columnList = "id_funcionario"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Publicacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_funcionario", nullable = false, foreignKey = @ForeignKey(name = "fk_publicacao_funcionario"))
    private Funcionario funcionario;
    @Column(nullable = false, length = 200)
    private String titulo;
    @Column(columnDefinition = "text")
    private String descricao;
    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;
}
