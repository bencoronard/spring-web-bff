package dev.hireben.demo.rest.web.bff.application.exception;

import dev.hireben.demo.rest.web.bff.application.model.ApplicationException;

public class AuthenticationFailedException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public AuthenticationFailedException(String message) {
    super(message);
  }

}
