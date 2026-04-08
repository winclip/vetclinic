package dev.winclip.vetclinic.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordChangeRequest(
		@NotBlank @Schema(example = "top_secret_password") String currentPassword,
		@NotBlank @Size(min = 8, max = 128) @Schema(example = "baba-yaga-top") String newPassword) {
}
