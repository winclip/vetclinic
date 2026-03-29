package dev.winclip.vetclinic.doctor.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
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

		@Size(max = 2000, message = "Bio must be at most 2000 characters long")
		String bio,

		@Size(max = 512, message = "Photo URL must be at most 512 characters long")
		String photoUrl,

		@Past(message = "Date of birth must be in the past")
		LocalDate dateOfBirth,

		@Min(value = 0, message = "Years of experience must be at least 0")
		@Max(value = 100, message = "Years of experience must be at most 100")
		Integer yearsOfExperience,

		LocalDate hiredOn,

		Boolean active) {
}
