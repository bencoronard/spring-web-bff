package dev.hireben.demo.web.bff.application.service;

import java.util.Optional;

import dev.hireben.demo.web.bff.domain.model.Uzer;

public interface AuthenticationService {

  Optional<Uzer> authenticate(String username, String password);

}
