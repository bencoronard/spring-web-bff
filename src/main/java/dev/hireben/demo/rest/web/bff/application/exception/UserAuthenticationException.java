package dev.hireben.demo.rest.web.bff.application.exception;

import dev.hireben.demo.rest.web.bff.application.model.ApplicationException;

public class UserAuthenticationException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public UserAuthenticationException(String message) {
    super(message);
  }

}
