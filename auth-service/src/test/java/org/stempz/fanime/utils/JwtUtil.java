package org.stempz.fanime.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

  private static final int EXPIRATION_PERIOD_OF_TOKENS = 1000 * 60 * 60 * 12;
  private static final Key SIGNING_KEY = getSigningKey();

  public static String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_PERIOD_OF_TOKENS))
        .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  public static String generateToken(String email, long expirationPeriod) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationPeriod))
        .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
        .compact();
  }

  private static Key getSigningKey() {
    return Keys.secretKeyFor(SignatureAlgorithm.HS256);
  }
}
