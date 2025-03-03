package dev.hireben.demo.web.bff.application.exception;

import dev.hireben.demo.web.bff.application.model.ApplicationException;

public class PublicViewAccessDeniedException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public PublicViewAccessDeniedException(String message) {
    super(message);
  }

}
