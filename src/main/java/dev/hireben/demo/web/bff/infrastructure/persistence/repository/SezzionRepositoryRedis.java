package dev.hireben.demo.web.bff.infrastructure.persistence.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import dev.hireben.demo.web.bff.domain.entity.Sezzion;
import dev.hireben.demo.web.bff.domain.repository.SezzionRepository;
import dev.hireben.demo.web.bff.infrastructure.persistence.redis.entity.SezzionEntity;
import dev.hireben.demo.web.bff.infrastructure.persistence.redis.mapper.SezzionEntityMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SezzionRepositoryRedis implements SezzionRepository {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final String KEY_FORMAT = "%s:%s";
  private final String PREFIX_SESSION_ID_KEY = "sezzion";
  private final String PREFIX_USER_ID_KEY = "uzer";

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
      executeInTransaction((sessionOperations, userSessionsOperations) -> {
        sessionOperations.expireAt(formatSessionKeyFrom(session.getId()), session.getExpiresAt());

        if (session.getUser() != null) {
          userSessionsOperations.expireAt(formatUserIdKeyFrom(session.getUser().getId()), session.getExpiresAt());
        }

        return null;
      });

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

    executeInTransaction((sessionOperations, userSessionsOperations) -> {
      sessionOperations.opsForValue().set(newSessionKey, newSession,
          Duration.between(Instant.now(), session.getExpiresAt()));

      if (session.getUser() != null) {
        String currentUserIdKey = formatUserIdKeyFrom(session.getUser().getId());
        userSessionsOperations.opsForSet().add(currentUserIdKey, newSessionId);
        userSessionsOperations.expireAt(currentUserIdKey, session.getExpiresAt());
      }

      return null;
    });

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

    executeInTransaction((sessionOperations, userSessionsOperations) -> {
      if (session.getUser() != null) {
        userSessionsOperations.opsForSet().remove(formatUserIdKeyFrom(session.getUser().getId()), id);
      }

      sessionOperations.delete(sessionKey);

      return null;
    });
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

  @Scheduled(fixedRateString = "${application.auth.session.cleanup-interval}")
  public void cleanUpExpiredSessionIds() {

    Collection<String> userSessionsKeys = userSessionsRedisTemplate.keys(formatUserIdKeyFrom("*"));

    if (userSessionsKeys == null || userSessionsKeys.isEmpty()) {
      return;
    }

    for (String userSessionsKey : userSessionsKeys) {
      Collection<String> sessionIds = userSessionsRedisTemplate.opsForSet().members(userSessionsKey);

      if (sessionIds == null || sessionIds.isEmpty()) {
        continue;
      }

      Collection<String> expiredSessionIds = sessionIds.stream()
          .filter(sessionId -> findById(sessionId).isEmpty())
          .toList();

      if (!expiredSessionIds.isEmpty()) {
        userSessionsRedisTemplate.opsForSet().remove(userSessionsKey,
            expiredSessionIds.toArray());
      }
    }

    System.out.println("Hello, world!");
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

  // ---------------------------------------------------------------------------//

  private <T> T executeInTransaction(
      BiFunction<RedisTemplate<String, SezzionEntity>, RedisTemplate<String, String>, T> callback) {
    return sessionRedisTemplate.execute(new SessionCallback<T>() {

      @Override
      @Nullable
      public <K, V> T execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
        operations.multi();
        T result = callback.apply(sessionRedisTemplate, userSessionsRedisTemplate);
        operations.exec();
        return result;
      }

    });
  }

}
