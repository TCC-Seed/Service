package com.tccseed.tcc_seed.service.formulariopersonalizado;

import com.tccseed.tcc_seed.domain.entity.PerguntaFormularioPersonalizado;
import com.tccseed.tcc_seed.repository.PerguntaFormularioPersonalizadoRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerguntaStoreService {
  private final PerguntaFormularioPersonalizadoRepository repository;
  private final EntityManager entityManager;

  @Transactional
  public Optional<PerguntaFormularioPersonalizado> criarSeAusente(String enunciado) {
    var ids =
        entityManager
            .createNativeQuery(
                "insert into pergunta_formulario_personalizado (enunciado) values (:enunciado) "
                    + "on conflict (lower(btrim(enunciado))) do nothing returning id")
            .setParameter("enunciado", enunciado)
            .getResultList();
    if (ids.isEmpty()) {
      return Optional.empty();
    }
    Number id = (Number) ids.getFirst();
    return repository.findById(id.longValue());
  }
}
