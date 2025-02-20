package dev.hireben.demo.rest.web.bff.application.model;

public abstract class ApplicationException extends RuntimeException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  protected ApplicationException(String message) {
    super(message);
  }

}
