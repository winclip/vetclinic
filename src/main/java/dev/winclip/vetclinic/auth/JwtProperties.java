package dev.winclip.vetclinic.auth;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vetclinic.jwt")
public record JwtProperties(
		String secret,
		Duration expiration
) {
}
