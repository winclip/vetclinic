package dev.winclip.vetclinic.appointment.dto;

import java.time.Instant;

import dev.winclip.vetclinic.appointment.Appointment;
import dev.winclip.vetclinic.appointment.AppointmentStatus;

public record AppointmentResponse(
		Long id,
		Long doctorId,
		Long petId,
		Instant startsAt,
		Instant endsAt,
		AppointmentStatus status,
		Instant createdAt) {

	public static AppointmentResponse from(Appointment a) {
		return new AppointmentResponse(
				a.getId(),
				a.getDoctor().getId(),
				a.getPet().getId(),
				a.getStartsAt(),
				a.getEndsAt(),
				a.getStatus(),
				a.getCreatedAt());
	}
}
