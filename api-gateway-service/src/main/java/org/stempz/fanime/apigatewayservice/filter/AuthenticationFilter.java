package org.stempz.fanime.apigatewayservice.filter;

import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.stempz.fanime.apigatewayservice.dto.AuthenticationResponseDto;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private static final String TOKEN_PREFIX = "Bearer ";

  private final WebClient.Builder webClientBuilder;

  public AuthenticationFilter( WebClient.Builder webClientBuilder) {
    super(Config.class);
    this.webClientBuilder = webClientBuilder;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      return extractBearer(request)
          .flatMap(this::validateToken)
          .map(response -> updateRequest(exchange, response))
          .defaultIfEmpty(request)
          .flatMap(updatedRequest -> chain.filter(exchange.mutate().request(updatedRequest).build()));
    };
  }

  private Mono<String> extractBearer(ServerHttpRequest request) {
    return Mono.justOrEmpty(request.getHeaders().get(HttpHeaders.AUTHORIZATION))
        .map(header -> header.get(0))
        .filter(token -> token.startsWith(TOKEN_PREFIX));
  }

  private Mono<AuthenticationResponseDto> validateToken(String bearer) {
    return webClientBuilder
        .build().get()
        .uri("http://auth-service/api/v1/auth/validate")
        .headers(headers -> headers.put(HttpHeaders.AUTHORIZATION, List.of(bearer)))
        .retrieve()
        .bodyToMono(AuthenticationResponseDto.class)
        .filter(AuthenticationResponseDto::authenticated);
  }

  private ServerHttpRequest updateRequest(ServerWebExchange exchange, AuthenticationResponseDto response) {
    return exchange.getRequest()
        .mutate()
        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + response.jwt())
        .build();
  }

  public static class Config {

  }
}
