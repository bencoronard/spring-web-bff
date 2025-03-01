package dev.hireben.demo.rest.web.bff.infrastructure.persistence.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import dev.hireben.demo.rest.web.bff.domain.entity.Sezzion;
import dev.hireben.demo.rest.web.bff.domain.repository.SezzionRepository;
import dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.entity.SezzionEntity;
import dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.mapper.SezzionEntityMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SezzionRepositoryRedis implements SezzionRepository {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private static final String PREFIX_SESSION_ID_KEY = "sezzion";
  private static final String PREFIX_USER_ID_KEY = "uzer";
  private static final String KEY_FORMAT = "%s:%s";

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final RedisTemplate<String, SezzionEntity> sessionRedisTemplate;
  private final RedisTemplate<String, String> userSessionsRedisTemplate;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  public Sezzion save(Sezzion session) {

    if (session.getId() != null) {
      sessionRedisTemplate.expireAt(formatSessionKeyFrom(session.getId()), session.getExpiresAt());
      return session;
    }

    String newSessionId = UUID.randomUUID().toString();

    SezzionEntity newSession = SezzionEntity.builder()
        .id(newSessionId)
        .user(session.getUser())
        .syncToken(session.getSyncToken())
        .createdAt(session.getCreatedAt())
        .build();

    String newSessionKey = formatSessionKeyFrom(newSessionId);

    sessionRedisTemplate.opsForValue().set(newSessionKey, newSession,
        Duration.between(Instant.now(), session.getExpiresAt()));

    if (session.getUser() == null) {
      return SezzionEntityMapper.toDomain(newSession, session.getExpiresAt());
    }

    String currentUserIdKey = formatUserIdKeyFrom(session.getUser().getId());

    userSessionsRedisTemplate.opsForSet().add(currentUserIdKey, newSessionId);
    userSessionsRedisTemplate.expireAt(currentUserIdKey, session.getExpiresAt());

    return SezzionEntityMapper.toDomain(newSession, session.getExpiresAt());
  }

  // ---------------------------------------------------------------------------//

  @Override
  public void deleteById(String id) {

    String sessionKey = formatSessionKeyFrom(id);

    SezzionEntity session = sessionRedisTemplate.opsForValue().get(sessionKey);

    if (session == null) {
      return;
    }

    if (session.getUser() != null) {
      sessionRedisTemplate.opsForSet().remove(formatUserIdKeyFrom(session.getUser().getId()), id);
    }

    sessionRedisTemplate.delete(sessionKey);
  }

  // ---------------------------------------------------------------------------//

  @Override
  public Optional<Sezzion> findById(String id) {

    String sessionKey = formatSessionKeyFrom(id);

    SezzionEntity session = sessionRedisTemplate.opsForValue().get(sessionKey);
    if (session == null) {
      return Optional.empty();
    }

    Long sessionTtl = sessionRedisTemplate.getExpire(sessionKey);
    if (sessionTtl == null || sessionTtl < 0) {
      throw new IllegalStateException(String.format("Illegal expiration time for session %s", id));
    }

    return Optional.of(SezzionEntityMapper.toDomain(session, Instant.now().plusSeconds(sessionTtl)));
  }

  // ---------------------------------------------------------------------------//

  @Override
  public Collection<Sezzion> findAllByUserId(String userId) {

    String userIdKey = formatUserIdKeyFrom(userId);

    Set<String> sessionIds = userSessionsRedisTemplate.opsForSet().members(userIdKey);

    if (sessionIds == null || sessionIds.isEmpty()) {
      return Collections.emptyList();
    }

    return sessionIds.stream()
        .map(sessionId -> findById(sessionId))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  // ---------------------------------------------------------------------------//
  // Helpers
  // ---------------------------------------------------------------------------//

  private String formatSessionKeyFrom(String sessionId) {
    return String.format(KEY_FORMAT, PREFIX_SESSION_ID_KEY, sessionId);
  }

  // ---------------------------------------------------------------------------//

  private String formatUserIdKeyFrom(String userId) {
    return String.format(KEY_FORMAT, PREFIX_USER_ID_KEY, userId);
  }

}
