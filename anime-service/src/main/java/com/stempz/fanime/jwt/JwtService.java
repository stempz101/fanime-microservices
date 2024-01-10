package com.stempz.fanime.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import com.stempz.fanime.model.enums.Role;

@Component
public class JwtService {

  @Value("${app.security.secret-key}")
  private String SECRET_KEY;

  public Optional<String> extractEmail(String token) {
    return Optional.ofNullable(extractClaim(token, Claims::getSubject));
  }

  public Optional<Long> extractId(String token) {
    return Optional.ofNullable(extractClaim(token, claims -> claims.get("userId", Integer.class)))
        .map(Long::valueOf);
  }

  public Optional<Role> extractRole(String token) {
    return Optional.ofNullable(extractClaim(token, claims -> claims.get("userRole", String.class)))
        .flatMap(this::getRole);
  }

  private Optional<Role> getRole(String extractedRole) {
    return Optional.ofNullable(extractedRole)
        .filter(role -> role.startsWith("ROLE_"))
        .map(role -> role.substring(5))
        .map(role -> role.equals(Role.ADMIN.name()) ? Role.ADMIN :
            role.equals(Role.USER.name()) ? Role.USER : null);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    return claimsResolver.apply(extractAllClaims(token));
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public Optional<String> extractBearerToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
        .filter(authHeader -> authHeader.startsWith("Bearer "))
        .map(authHeader -> authHeader.substring(7));
  }
}
