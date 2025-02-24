package dev.hireben.demo.rest.web.bff.utility;

import java.util.Optional;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestUtil {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public Optional<String> extractHeader(HttpServletRequest request, String key) {
    String header = request.getHeader(key);

    if (header != null && !header.isBlank()) {
      return Optional.of(header);
    }

    return Optional.empty();
  }

  // ---------------------------------------------------------------------------//

  public <T> Optional<T> extractAttribute(HttpServletRequest request, String key, Class<T> type) {
    Object attr = request.getAttribute(key);

    if (attr != null && type.isInstance(attr)) {
      return Optional.of(type.cast(attr));
    }

    return Optional.empty();
  }

  // ---------------------------------------------------------------------------//

  public <T> Optional<T> extractAttribute(WebRequest request, String key, Class<T> type) {
    Object attr = request.getAttribute(key, RequestAttributes.SCOPE_REQUEST);

    if (attr != null && type.isInstance(attr)) {
      return Optional.of(type.cast(attr));
    }

    return Optional.empty();
  }

}
