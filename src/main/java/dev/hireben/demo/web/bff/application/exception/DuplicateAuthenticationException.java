package dev.hireben.demo.web.bff.application.exception;

import dev.hireben.demo.web.bff.application.model.ApplicationException;

public class DuplicateAuthenticationException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public DuplicateAuthenticationException(String message) {
    super(message);
  }

}
