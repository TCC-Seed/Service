package com.tccseed.tcc_seed.domain.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "funcionario", indexes = @Index(name = "idx_funcionario_id_ies", columnList = "id_ies"))
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Funcionario extends Usuario {

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 200)
    private String formacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ies", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_funcionario_ies"))
    @EqualsAndHashCode.Exclude
    private Ies ies;
}
