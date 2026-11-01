package com.tccseed.tcc_seed.repository;

import com.tccseed.tcc_seed.domain.entity.ItemRespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.ItemRespostaFormularioPersonalizadoId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
