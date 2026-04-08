package dev.winclip.vetclinic.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserMeUpdateRequest(
		@Email @Size(max = 255) @Schema(example = "new.john.wick@example.com") String email,
		@Size(max = 200) @Schema(example = "John Wick") String fullName
) {

	@AssertTrue(message = "At least one of email or fullName must be provided")
	public boolean isAtLeastOneNonBlank() {
		boolean emailOk = email != null && !email.isBlank();
		boolean nameOk = fullName != null && !fullName.isBlank();
		return emailOk || nameOk;
	}
}
