package dev.hireben.demo.rest.web.bff.presentation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.hireben.demo.rest.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.rest.web.bff.application.service.PermissionService;
import dev.hireben.demo.rest.web.bff.application.usecase.AuthenticationUseCase;
import dev.hireben.demo.rest.web.bff.application.usecase.AuthorizationUseCase;

@Configuration
public class UseCaseConfig {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  AuthenticationUseCase authenticationUseCase(AuthenticationService authenticationService) {
    return new AuthenticationUseCase(authenticationService);
  }

  // ---------------------------------------------------------------------------//

  @Bean
  AuthorizationUseCase authorizationUseCase(PermissionService permissionService) {
    return new AuthorizationUseCase(permissionService);
  }

}
