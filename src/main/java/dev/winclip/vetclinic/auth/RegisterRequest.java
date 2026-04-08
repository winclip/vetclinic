package dev.winclip.vetclinic.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Size(min = 4, max = 64) @Schema(example = "john_wick") String username,
		@NotBlank @Size(min = 8, max = 128) @Schema(example = "top_secret_password") String password,
		@NotBlank @Email @Size(max = 255) @Schema(example = "john.wick@example.com") String email,
		@NotBlank @Size(max = 200) @Schema(example = "John Wick") String fullName) {
}
