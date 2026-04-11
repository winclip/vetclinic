package dev.winclip.vetclinic.doctor.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DoctorWorkingHoursItemRequest(
		@Min(1) @Max(7) int dayOfWeek,
		@NotNull LocalTime startTime,
		@NotNull LocalTime endTime) {
}
