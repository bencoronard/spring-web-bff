package dev.hireben.demo.rest.web.bff.infrastructure.persistence.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.stereotype.Repository;

import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;
import dev.hireben.demo.rest.web.bff.domain.repository.UserSessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSessionRepositoryRedis implements UserSessionRepository {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  @SuppressWarnings("unused")
  private final FindByIndexNameSessionRepository<?> sessionRepository;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  public UserSession save(UserSession session) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'save'");
  }

  // ---------------------------------------------------------------------------//

  @Override
  public void deleteById(String id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

  // ---------------------------------------------------------------------------//

  @Override
  public Optional<UserSession> findById(String id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  // ---------------------------------------------------------------------------//

  @Override
  public Collection<UserSession> findAllByUserId(String userId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAllByUserId'");
  }

}
