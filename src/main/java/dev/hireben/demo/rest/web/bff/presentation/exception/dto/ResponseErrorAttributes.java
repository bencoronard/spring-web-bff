package dev.hireben.demo.rest.web.bff.presentation.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "message", "exception", "data" })
public class ResponseErrorAttributes<T> {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  private final String code;
  private final String message;
  private final String exception;
  private final T data;

}
