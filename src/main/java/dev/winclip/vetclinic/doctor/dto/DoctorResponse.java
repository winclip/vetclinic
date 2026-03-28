package dev.winclip.vetclinic.doctor.dto;

import java.time.Instant;
import java.time.LocalDate;

import dev.winclip.vetclinic.doctor.Doctor;

public record DoctorResponse(
		Long id,
		String firstName,
		String lastName,
		String specialization,
		String phone,
		String email,
		String veterinaryLicense,
		LocalDate hiredOn,
		boolean active,
		Instant createdAt) {

	public static DoctorResponse from(Doctor d) {
		return new DoctorResponse(
				d.getId(),
				d.getFirstName(),
				d.getLastName(),
				d.getSpecialization(),
				d.getPhone(),
				d.getEmail(),
				d.getVeterinaryLicense(),
				d.getHiredOn(),
				d.isActive(),
				d.getCreatedAt());
	}
}
