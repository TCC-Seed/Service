package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.Publicacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublicacaoRepository extends JpaRepository<Publicacao, Long> {
    @Override
    <T extends Publicacao> T save(T entity);

    @Override
    Optional<Publicacao> findById(Long id);

    @Override
    List<Publicacao> findAll();

    @Override
    void deleteById(Long id);
}
