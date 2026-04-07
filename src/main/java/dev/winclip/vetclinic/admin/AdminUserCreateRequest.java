package dev.winclip.vetclinic.admin;

import dev.winclip.vetclinic.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminUserCreateRequest(
		@NotBlank @Size(min = 4, max = 64) String username,
		@NotBlank @Size(min = 8, max = 128) String password,
		@NotBlank @Email @Size(max = 255) String email,
		@NotBlank @Size(max = 200) String fullName,
		@NotNull UserRole role
) {
}
