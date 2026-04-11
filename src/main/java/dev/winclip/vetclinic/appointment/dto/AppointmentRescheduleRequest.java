package dev.winclip.vetclinic.appointment.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record AppointmentRescheduleRequest(
		@NotNull Instant startsAt) {
}
