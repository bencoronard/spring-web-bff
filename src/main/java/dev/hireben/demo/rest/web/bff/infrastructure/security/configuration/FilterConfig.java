package dev.hireben.demo.rest.web.bff.infrastructure.security.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.hireben.demo.rest.web.bff.infrastructure.security.filter.ApiKeyFilter;
import dev.hireben.demo.rest.web.bff.infrastructure.security.filter.RequestLoggingFilter;
import dev.hireben.demo.rest.web.bff.utility.EnvironmentUtil;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

  // ---------------------------------------------------------------------------//
  // Dependencies
  // ---------------------------------------------------------------------------//

  private final EnvironmentUtil environment;

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter() {
    FilterRegistrationBean<RequestLoggingFilter> filter = new FilterRegistrationBean<>();
    filter.setFilter(new RequestLoggingFilter(environment.isDev()));
    filter.setOrder(0);
    filter.addUrlPatterns("/*");
    return filter;
  }

  // ---------------------------------------------------------------------------//

  @Bean
  FilterRegistrationBean<ApiKeyFilter> apiKeyFilter(@Value("${server.secret-key}") String apiKey) {
    FilterRegistrationBean<ApiKeyFilter> filter = new FilterRegistrationBean<>();
    filter.setFilter(new ApiKeyFilter(apiKey));
    filter.setOrder(1);
    filter.addUrlPatterns("/*");
    return filter;
  }

}
