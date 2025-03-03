package dev.hireben.demo.web.bff.infrastructure.http.configurations;

import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

  // ---------------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------------//

  // @Bean
  // ClientHttpRequestFactory clientHttpRequestFactory(
  // @Value("${application.http.client.timeout-in-sec}") int connTimeout,
  // @Value("${application.http.client.pool-size}") int connPoolSize,
  // @Value("${application.http.client.max-conn-per-route}") int maxConnPerRoute)
  // {

  // PoolingHttpClientConnectionManager connectionManager = new
  // PoolingHttpClientConnectionManager();
  // connectionManager.setMaxTotal(connPoolSize);
  // connectionManager.setDefaultMaxPerRoute(maxConnPerRoute);

  // CloseableHttpClient httpClient =
  // HttpClients.custom().setConnectionManager(connectionManager).build();

  // HttpComponentsClientHttpRequestFactory factory = new
  // HttpComponentsClientHttpRequestFactory(httpClient);
  // factory.setConnectTimeout(connTimeout * 1000);

  // return factory;
  // }

  // ---------------------------------------------------------------------------//

  // @Bean
  // RestClient restClient(
  // RestClient.Builder builder,
  // ClientHttpRequestFactory clientHttpRequestFactory,
  // ResponseErrorHandler responseErrorHandler) {
  // return builder
  // .requestFactory(clientHttpRequestFactory)
  // .defaultStatusHandler(responseErrorHandler)
  // .build();
  // }

}
