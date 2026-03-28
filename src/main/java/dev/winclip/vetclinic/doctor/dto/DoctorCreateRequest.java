package dev.winclip.vetclinic.doctor.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorCreateRequest(
		@NotBlank @Size(max = 100) String firstName,
		@NotBlank @Size(max = 100) String lastName,
		@Size(max = 255) String specialization,
		@Size(max = 32) String phone,
		@Email @Size(max = 255) String email,
		@Size(max = 64) String veterinaryLicense,
		LocalDate hiredOn,
		Boolean active) {
}
