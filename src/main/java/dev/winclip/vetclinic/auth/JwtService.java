package dev.winclip.vetclinic.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private static final String CLAIM_ROLE = "role";

	private final JwtProperties properties;
	private SecretKey key;

	@PostConstruct
	void initKey() {
		byte[] bytes = properties.secret().getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("vetclinic.jwt.secret must be at least 32 bytes for HS256");
		}
		this.key = Keys.hmacShaKeyFor(bytes);
	}

	public String generateToken(String username, String roleName) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(username)
				.claim(CLAIM_ROLE, roleName)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(properties.expiration())))
				.signWith(key)
				.compact();
	}

	public Optional<Authentication> parseBearerToken(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			return Optional.empty();
		}
		try {
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(rawToken.trim())
					.getPayload();
			String username = claims.getSubject();
			String role = claims.get(CLAIM_ROLE, String.class);
			if (username == null || username.isBlank() || role == null || role.isBlank()) {
				return Optional.empty();
			}
			var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
			return Optional.of(UsernamePasswordAuthenticationToken.authenticated(username, null, authorities));
		} catch (JwtException | IllegalArgumentException ex) {
			return Optional.empty();
		}
	}
}
