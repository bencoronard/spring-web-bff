package dev.hireben.demo.rest.web.bff.application.exception;

import dev.hireben.demo.rest.web.bff.application.model.ApplicationException;

public class ProtectedViewAccessDeniedException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public ProtectedViewAccessDeniedException(String message) {
    super(message);
  }

}
