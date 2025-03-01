package dev.hireben.demo.rest.web.bff.application.exception;

import dev.hireben.demo.rest.web.bff.application.model.ApplicationException;

public class InvalidSyncTokenException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public InvalidSyncTokenException(String message) {
    super(message);
  }

}
