package dev.hireben.demo.rest.web.bff.application.service;

import java.util.Optional;

import dev.hireben.demo.rest.web.bff.domain.model.Uzer;

public interface AuthenticationService {

  Optional<Uzer> authenticate(String username, String password);

}
