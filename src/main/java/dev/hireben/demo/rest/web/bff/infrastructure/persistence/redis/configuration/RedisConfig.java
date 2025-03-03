package dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import dev.hireben.demo.rest.web.bff.infrastructure.persistence.redis.entity.SezzionEntity;

@Configuration
public class RedisConfig {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  RedisTemplate<String, SezzionEntity> sessionRedisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, SezzionEntity> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(SezzionEntity.class));

    template.afterPropertiesSet();
    return template;
  }

  // ---------------------------------------------------------------------------//

  @Bean
  RedisTemplate<String, String> userSessionsRedisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());

    template.afterPropertiesSet();
    return template;
  }

}
