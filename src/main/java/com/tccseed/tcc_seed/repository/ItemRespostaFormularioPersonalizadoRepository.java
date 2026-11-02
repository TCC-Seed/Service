package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.ItemRespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.ItemRespostaFormularioPersonalizadoId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRespostaFormularioPersonalizadoRepository
    extends JpaRepository<
        ItemRespostaFormularioPersonalizado, ItemRespostaFormularioPersonalizadoId> {
  @Override
  <T extends ItemRespostaFormularioPersonalizado> T save(T entity);

  @Override
  Optional<ItemRespostaFormularioPersonalizado> findById(ItemRespostaFormularioPersonalizadoId id);

  @Override
  List<ItemRespostaFormularioPersonalizado> findAll();

  @Override
  void deleteById(ItemRespostaFormularioPersonalizadoId id);

  @Query(
      "select i from ItemRespostaFormularioPersonalizado i join fetch i.pergunta where i.respostaFormulario.id in :ids and exists (select fp.id from FormularioPergunta fp where fp.formulario.id = i.respostaFormulario.formulario.id and fp.pergunta.id = i.pergunta.id) order by i.respostaFormulario.id asc, i.pergunta.id asc")
  List<ItemRespostaFormularioPersonalizado> findWithPerguntasByRespostaIds(
      @Param("ids") List<Long> ids);

  void deleteByRespostaFormularioId(Long respostaId);
}
