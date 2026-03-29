package dev.winclip.vetclinic.auth;

public record LoginResponse(
		String accessToken,
		String tokenType
) {
}
