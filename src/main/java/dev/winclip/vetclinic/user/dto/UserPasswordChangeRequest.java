package dev.winclip.vetclinic.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordChangeRequest(
		@NotBlank String currentPassword,
		@NotBlank @Size(min = 8, max = 128) String newPassword
) {
}
