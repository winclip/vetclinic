package dev.winclip.vetclinic.appointment.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record AppointmentCreateRequest(
		@NotNull Long doctorId,
		@NotNull Long petId,
		@NotNull Instant startsAt) {
}
