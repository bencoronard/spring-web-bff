package dev.hireben.demo.rest.web.bff.infrastructure.security.filter;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import dev.hireben.demo.rest.web.bff.presentation.model.HttpHeaderKey;
import dev.hireben.demo.rest.web.bff.presentation.model.RequestAttributeKey;
import dev.hireben.demo.rest.web.bff.utility.LogFormatUtil;
import dev.hireben.demo.rest.web.bff.utility.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLoggingFilter extends CommonsRequestLoggingFilter {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final boolean shouldLog;

  // ---------------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------------//

  public RequestLoggingFilter(boolean shouldLog) {
    this.shouldLog = shouldLog;
    setIncludeQueryString(true);
    setIncludeHeaders(false);
    setIncludePayload(false);
    setMaxPayloadLength(500);
    setBeforeMessagePrefix("");
    setBeforeMessageSuffix("");
  }

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Override
  protected boolean shouldLog(@NonNull HttpServletRequest request) {
    return shouldLog;
  }

  // ---------------------------------------------------------------------------//

  @Override
  protected void beforeRequest(@NonNull HttpServletRequest request, @NonNull String message) {

    String traceId = RequestUtil.extractHeader(request, HttpHeaderKey.TRACE_ID).orElse(request.getRequestId());

    request.setAttribute(RequestAttributeKey.TRACE_ID, traceId);

    log.info(LogFormatUtil.formatTraceLog(traceId, message));
  }

}
