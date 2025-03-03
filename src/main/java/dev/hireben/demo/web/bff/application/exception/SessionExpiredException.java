package dev.hireben.demo.web.bff.application.exception;

import dev.hireben.demo.web.bff.application.model.ApplicationException;

public class SessionExpiredException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public SessionExpiredException(String message) {
    super(message);
  }

}
