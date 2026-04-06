package dev.winclip.vetclinic.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserMeUpdateRequest(
		@Email @Size(max = 255) String email,
		@Size(max = 200) String fullName
) {

	@AssertTrue(message = "At least one of email or fullName must be provided")
	public boolean isAtLeastOneNonBlank() {
		boolean emailOk = email != null && !email.isBlank();
		boolean nameOk = fullName != null && !fullName.isBlank();
		return emailOk || nameOk;
	}
}
