package dev.hireben.demo.rest.web.bff.domain.repository;

import java.util.Optional;

import dev.hireben.demo.rest.web.bff.domain.entity.UserSession;

public interface UserSessionRepository {

  UserSession save(UserSession session);

  void delete(UserSession session);

  Optional<UserSession> findById(String id);

}
