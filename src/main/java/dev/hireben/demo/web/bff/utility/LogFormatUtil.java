package dev.hireben.demo.web.bff.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogFormatUtil {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final String TRACABLE_LOG_FORMAT = "[trace: %s] %s";
  private final String ERROR_LOG_FORMAT = "%s{code=%s, message=%s}";

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  public String formatTraceLog(String traceId, String message) {
    return String.format(TRACABLE_LOG_FORMAT, traceId, message);
  }

  // ---------------------------------------------------------------------------//

  public String formatErrorLog(String className, String code, String message) {
    return String.format(ERROR_LOG_FORMAT, className, code, message);
  }

}
