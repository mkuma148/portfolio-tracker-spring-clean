package com.crypto.tracker.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
	private final Key key;
	private final long expirationMs;

	public JwtUtil(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.expirationMs = expirationMs;
	}

	public String generateToken(String subject, Map<String, Object> claims) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date()).setExpiration(exp)
				.signWith(key).compact();
	}

	public String extractUsername(String token) {
		return getClaims(token).getSubject();
	}

	public boolean isTokenExpired(String token) {
		return getClaims(token).getExpiration().before(new Date());
	}

	public Claims getClaims(String token) {

		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("Empty JWT");
		}

		// ✅ JWT must contain 2 dots
		if (token.chars().filter(ch -> ch == '.').count() != 2) {
			throw new IllegalArgumentException("Invalid JWT format");
		}

		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public boolean validateToken(String token) {
		try {
			getClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public Key getKey() {
		return key;
	}
}

