package dev.hireben.demo.rest.web.bff.application.service;

import java.util.Optional;

import dev.hireben.demo.rest.web.bff.domain.model.UserInfo;

public interface AuthenticationService {

  Optional<UserInfo> authenticate(String username, String password);

}
