package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.enums.Genero;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;

@Entity
@Table(name = "estudante", indexes = @Index(name = "idx_estudante_ies", columnList = "ies_id"))
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Estudante extends Usuario {

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(nullable = false)
    private LocalDate nascimento;

    @ColumnTransformer(write = "?::genero")
    @Column(columnDefinition = "genero")
    private Genero genero;

    @Column(name = "pais_origem", nullable = false, length = 50)
    private String paisOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ies_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_estudante_ies"))
    @EqualsAndHashCode.Exclude
    private Ies ies;
}
