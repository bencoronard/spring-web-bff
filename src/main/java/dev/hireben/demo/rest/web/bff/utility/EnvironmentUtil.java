package dev.hireben.demo.rest.web.bff.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtil {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final boolean isDev;

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  protected EnvironmentUtil(@Value("${spring.profiles.active}") String env) {
    isDev = "dev".equalsIgnoreCase(env);
  }

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public boolean isDev() {
    return isDev;
  }

}
