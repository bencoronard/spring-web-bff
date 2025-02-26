package dev.hireben.demo.rest.web.bff.application.exception;

import dev.hireben.demo.rest.web.bff.application.model.ApplicationException;

public class DeniedPermissionException extends ApplicationException {

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public DeniedPermissionException(String message) {
    super(message);
  }

}
