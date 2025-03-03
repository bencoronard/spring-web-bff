package dev.hireben.demo.web.bff.domain.repository;

import java.util.Collection;
import java.util.Optional;

import dev.hireben.demo.web.bff.domain.entity.Sezzion;

public interface SezzionRepository {

  Sezzion save(Sezzion session);

  void deleteById(String id);

  Optional<Sezzion> findById(String id);

  Collection<Sezzion> findAllByUserId(String userId);

}
