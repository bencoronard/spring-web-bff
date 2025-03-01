package dev.hireben.demo.rest.web.bff.presentation.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.hireben.demo.rest.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.rest.web.bff.application.service.PermissionService;
import dev.hireben.demo.rest.web.bff.application.service.SyncTokenService;
import dev.hireben.demo.rest.web.bff.application.usecase.AuthenticationUseCase;
import dev.hireben.demo.rest.web.bff.application.usecase.AuthorizationUseCase;
import dev.hireben.demo.rest.web.bff.domain.repository.SezzionRepository;

@Configuration
public class UseCaseConfig {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  AuthenticationUseCase authenticationUseCase(
      SezzionRepository sessionRepository,
      SyncTokenService syncTokenService,
      AuthenticationService authenService,
      @Value("${application.auth.session.max-per-user}") int maxActiveUserSessions,
      @Value("${application.auth.session.user-ttl}") int userSessionTtl) {
    return new AuthenticationUseCase(maxActiveUserSessions, userSessionTtl, sessionRepository, authenService,
        syncTokenService);
  }

  // ---------------------------------------------------------------------------//

  @Bean
  AuthorizationUseCase authorizationUseCase(
      SezzionRepository sessionRepository,
      PermissionService permissionService,
      @Value("${application.auth.session.anonymous-ttl}") int anonymousSessionTtl) {
    return new AuthorizationUseCase(anonymousSessionTtl, sessionRepository, permissionService);
  }

}
