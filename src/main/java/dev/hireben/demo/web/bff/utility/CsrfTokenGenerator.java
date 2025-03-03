package dev.hireben.demo.web.bff.utility;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

import dev.hireben.demo.web.bff.application.service.SyncTokenService;

@Service
public class CsrfTokenGenerator implements SyncTokenService {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final int TOKEN_LENGTH = 32;
  private final SecureRandom secureRandom = new SecureRandom();

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  public String generate() {
    byte[] randomBytes = new byte[TOKEN_LENGTH];
    secureRandom.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

}
