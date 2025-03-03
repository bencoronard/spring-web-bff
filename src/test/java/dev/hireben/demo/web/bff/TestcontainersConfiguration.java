package dev.hireben.demo.web.bff;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import jakarta.annotation.PreDestroy;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  @SuppressWarnings("resource")
  private final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
      .withExposedPorts(6379);

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public TestcontainersConfiguration() {
    redisContainer.start();
  }

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  @ServiceConnection(name = "redis")
  GenericContainer<?> redisContainer() {
    return redisContainer;
  }

  // ---------------------------------------------------------------------------//

  @PreDestroy
  public void stopContainer() {
    redisContainer.stop();
  }

}
