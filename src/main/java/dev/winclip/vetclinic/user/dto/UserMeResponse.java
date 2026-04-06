package dev.winclip.vetclinic.user.dto;

public record UserMeResponse(
		Long id,
		String username,
		String email,
		String fullName,
		String role
) {
}
