package dev.hireben.demo.web.bff.infrastructure.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.hireben.demo.web.bff.application.service.AuthenticationService;
import dev.hireben.demo.web.bff.domain.model.Uzer;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  public Optional<Uzer> authenticate(String username, String password) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'authenticate'");
  }

}
