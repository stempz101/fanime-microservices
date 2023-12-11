package org.stempz.fanime.animeservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.stempz.fanime.animeservice.model.enums.Role;
import org.stempz.fanime.animeservice.model.security.User;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    jwtService.extractBearerToken(request).ifPresent(token -> {
      Optional<Long> idOpt = jwtService.extractId(token);
      Optional<String> emailOpt = jwtService.extractEmail(token);
      Optional<Role> roleOpt = jwtService.extractRole(token);

      if (idOpt.isPresent() && emailOpt.isPresent() && roleOpt.isPresent()) {
        User user = new User(idOpt.get(), emailOpt.get(), roleOpt.get());
        if (!jwtService.isTokenExpired(token)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              user,
              null,
              user.getAuthorities()
          );
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    });

    filterChain.doFilter(request, response);
  }

}
