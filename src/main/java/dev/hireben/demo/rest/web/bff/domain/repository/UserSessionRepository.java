package dev.hireben.demo.rest.web.bff.domain.repository;

import java.util.Collection;
import java.util.Optional;

import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;

public interface UserSessionRepository {

  UserSession save(UserSession session);

  void deleteById(String id);

  Optional<UserSession> findById(String id);

  Collection<UserSession> findAllByUserId(String userId);

}
