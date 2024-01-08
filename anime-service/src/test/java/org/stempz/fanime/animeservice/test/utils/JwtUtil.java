package org.stempz.fanime.animeservice.test.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

  private static final int EXPIRATION_PERIOD_OF_TOKENS = 1000 * 60 * 60 * 12;
  private static final String SECRET_KEY = "2A472D4A614E645267556B58703273357638792F423F4528482B4D6250655368";

  public static String generateToken(Map<String, Object> claims, String email) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_PERIOD_OF_TOKENS))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private static Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
