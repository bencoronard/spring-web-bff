package dev.hireben.demo.rest.web.bff.infrastructure.persistence.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import dev.hireben.demo.rest.web.bff.domain.entity.Sezzion;
import dev.hireben.demo.rest.web.bff.domain.repository.SezzionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SezzionRepositoryRedis implements SezzionRepository {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  public Sezzion save(Sezzion session) {
    return session;
  }

  // ---------------------------------------------------------------------------//

  @Override
  public void deleteById(String id) {

  }

  // ---------------------------------------------------------------------------//

  @Override
  public Optional<Sezzion> findById(String id) {
    return null;
  }

  // ---------------------------------------------------------------------------//

  @Override
  public Collection<Sezzion> findAllByUserId(String userId) {
    return null;
  }

}
