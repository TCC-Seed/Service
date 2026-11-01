package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.CategoriaPublicacao;
import com.tccseed.tcc_seed.domain.entity.id.CategoriaPublicacaoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaPublicacaoRepository extends JpaRepository<CategoriaPublicacao, CategoriaPublicacaoId> {
    @Override
    <T extends CategoriaPublicacao> T save(T entity);

    @Override
    Optional<CategoriaPublicacao> findById(CategoriaPublicacaoId id);

    @Override
    List<CategoriaPublicacao> findAll();

    @Override
    void deleteById(CategoriaPublicacaoId id);
}
