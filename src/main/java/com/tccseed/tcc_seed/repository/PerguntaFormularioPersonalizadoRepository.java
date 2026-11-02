package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerguntaFormularioPersonalizadoRepository
    extends JpaRepository<PerguntaFormularioPersonalizado, Long> {
  @Override
  <T extends PerguntaFormularioPersonalizado> T save(T entity);

  @Override
  Optional<PerguntaFormularioPersonalizado> findById(Long id);

  @Override
  List<PerguntaFormularioPersonalizado> findAll();

  @Override
  void deleteById(Long id);

  @Query(
      "select p from PerguntaFormularioPersonalizado p where lower(trim(p.enunciado)) = :normalizado")
  Optional<PerguntaFormularioPersonalizado> findByEnunciadoNormalizado(
      @Param("normalizado") String normalizado);

  @Query(
      "select p from PerguntaFormularioPersonalizado p where :busca = '' or lower(p.enunciado) like lower(concat('%', :busca, '%')) order by p.id")
  Page<PerguntaFormularioPersonalizado> buscar(@Param("busca") String busca, Pageable pageable);
}
