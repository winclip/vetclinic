package dev.winclip.vetclinic.doctor.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorCreateRequest(
		@NotBlank(message = "First name is required")
		@Size(max = 100, message = "First name must be at most 100 characters long")
		String firstName,
		@NotBlank(message = "Last name is required")
		@Size(max = 100, message = "Last name must be at most 100 characters long")
		String lastName,
		@Size(max = 255, message = "Specialization must be at most 255 characters long")
		String specialization,
		@Size(max = 32, message = "Phone must be at most 32 characters long")
		String phone,
		@Email(message = "Email must be a valid address")
		@Size(max = 255, message = "Email must be at most 255 characters long")
		String email,
		@Size(max = 64, message = "Veterinary license must be at most 64 characters long")
		String veterinaryLicense,
		LocalDate hiredOn,
		Boolean active) {
}
