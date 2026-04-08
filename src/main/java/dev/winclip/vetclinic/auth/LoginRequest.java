package dev.winclip.vetclinic.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank @Schema(example = "john_wick") String username,
		@NotBlank @Schema(example = "top_secret_password") String password) {
}
