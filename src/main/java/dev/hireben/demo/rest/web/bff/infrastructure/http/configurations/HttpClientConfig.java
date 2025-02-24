package dev.hireben.demo.rest.web.bff.infrastructure.http.configurations;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  @Bean
  ClientHttpRequestFactory clientHttpRequestFactory(
      @Value("${application.http.client.timeout-in-sec}") int connTimeout,
      @Value("${application.http.client.pool-size}") int connPoolSize,
      @Value("${application.http.client.max-conn-per-route}") int maxConnPerRoute) {

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(connPoolSize);
    connectionManager.setDefaultMaxPerRoute(maxConnPerRoute);

    CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    factory.setConnectTimeout(connTimeout * 1000);

    return factory;
  }

  // ---------------------------------------------------------------------------//

  @Bean
  RestClient restClient(
      RestClient.Builder builder,
      ClientHttpRequestFactory clientHttpRequestFactory,
      ResponseErrorHandler responseErrorHandler) {
    return builder
        .requestFactory(clientHttpRequestFactory)
        .defaultStatusHandler(responseErrorHandler)
        .build();
  }

}
