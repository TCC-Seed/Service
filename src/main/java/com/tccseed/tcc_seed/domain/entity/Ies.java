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
@Table(name = "ies")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Ies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "chave_vinculo", nullable = false, length = 43)
    private String chaveVinculo;

    @Column(name = "regiao_administrativa", length = 100)
    private String regiaoAdministrativa;
}
