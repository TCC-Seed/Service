package com.tccseed.tcc_seed.domain.entity;

import com.tccseed.tcc_seed.domain.enums.TipoUsuario;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "email")
    private String email;

    @Column(nullable = false, columnDefinition = "char(50)")
    private String username;

    @Column(nullable = false, columnDefinition = "text")
    private String senha;

    @ColumnTransformer(write = "?::tipo_usuario")
    @Column(name = "tipo", nullable = false, columnDefinition = "tipo_usuario")
    private TipoUsuario tipo;
}