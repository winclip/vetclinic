package dev.winclip.vetclinic.auth;

public record RegisterResponse(
		String username,
		String role,
		String email,
		String fullName
) {
}
