package dev.hireben.demo.rest.web.bff.application.service;

import java.util.Optional;

import dev.hireben.demo.rest.web.bff.domain.model.UserDetails;

public interface AuthenticationService {

  Optional<UserDetails> authenticate(String username, String password);

}
