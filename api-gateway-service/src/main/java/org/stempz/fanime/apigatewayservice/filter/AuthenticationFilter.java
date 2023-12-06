package org.stempz.fanime.apigatewayservice.filter;

import java.util.Optional;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.stempz.fanime.apigatewayservice.dto.AuthenticationResponseDto;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final WebClient.Builder webClientBuilder;

  public AuthenticationFilter( WebClient.Builder webClientBuilder) {
    super(Config.class);
    this.webClientBuilder = webClientBuilder;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = null;

      Optional<AuthenticationResponseDto> responseOpt = extractBearer(exchange.getRequest())
          .flatMap(this::validateToken);

      if (responseOpt.isPresent()) {
        request = updateRequest(exchange, responseOpt.get());
      }

      return chain.filter(exchange.mutate().request(request).build());
    };
  }

  private Optional<String> extractBearer(ServerHttpRequest request) {
    return Optional.of(request.getHeaders())
        .map(headers -> headers.get(HttpHeaders.AUTHORIZATION))
        .map(header -> header.get(0))
        .filter(token -> token.startsWith("Bearer "));
  }

  private Optional<AuthenticationResponseDto> validateToken(String bearer) {
    return webClientBuilder.build().get()
        .uri("http://auth-service/api/v1/validate")
        .header(HttpHeaders.AUTHORIZATION, bearer)
        .retrieve()
        .bodyToMono(AuthenticationResponseDto.class)
        .blockOptional()
        .filter(AuthenticationResponseDto::authenticated);
  }

  private ServerHttpRequest updateRequest(ServerWebExchange exchange, AuthenticationResponseDto response) {
    return exchange.getRequest()
        .mutate()
        .header("loggedInUserEmail", response.email())
        .build();
  }

  public static class Config {

  }
}
