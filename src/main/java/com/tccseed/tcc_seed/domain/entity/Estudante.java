package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.enums.Genero;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;

@Entity
@Table(name = "estudante")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Estudante extends Usuario {

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, columnDefinition = "char(30)")
    private String matricula;

    @Column(nullable = false)
    private LocalDate nascimento;

    @ColumnTransformer(write = "?::genero")
    @Column(columnDefinition = "genero")
    private Genero genero;

    @Column(name = "pais_origem", nullable = false, columnDefinition = "char(50)")
    private String paisOrigem;
}
