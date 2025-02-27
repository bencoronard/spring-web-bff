package dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.entity;

import java.io.Serializable;

import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("UserSessionEntity")
public class UserSessionEntity extends Session implements Serializable {

  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private String id;

}
