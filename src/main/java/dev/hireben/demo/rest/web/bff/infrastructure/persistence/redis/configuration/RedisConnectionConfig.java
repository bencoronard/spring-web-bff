package dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.model.RedisConnectionMode;

@Configuration
public class RedisConnectionConfig {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  RedisConnectionFactory redisConnectionFactory(@Value("${DEMO_DATABASE_REDIS_CONN_MODE}") String redisConnMode) {
    RedisConnectionMode connectionMode = RedisConnectionMode.valueOf(redisConnMode.toUpperCase());
    return switch (connectionMode) {
      case CLUSTER -> redisClusterConnectionFactory();
      case SENTINEL -> redisSentinelConnectionFactory();
      case STANDALONE -> redisStandaloneConnectionFactory();
    };
  }

  // ---------------------------------------------------------------------------//

  private RedisConnectionFactory redisStandaloneConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName("localhost");
    config.setPort(6379);
    return new LettuceConnectionFactory(config);
  }

  // ---------------------------------------------------------------------------//

  private RedisConnectionFactory redisClusterConnectionFactory() {
    RedisClusterConfiguration config = new RedisClusterConfiguration(
        List.of("127.0.0.1:6379", "127.0.0.2:6379"));
    return new LettuceConnectionFactory(config);
  }

  // ---------------------------------------------------------------------------//

  private RedisConnectionFactory redisSentinelConnectionFactory() {
    RedisSentinelConfiguration config = new RedisSentinelConfiguration()
        .master("mymaster")
        .sentinel("127.0.0.1", 26379)
        .sentinel("127.0.0.2", 26379);
    return new LettuceConnectionFactory(config);
  }

}
