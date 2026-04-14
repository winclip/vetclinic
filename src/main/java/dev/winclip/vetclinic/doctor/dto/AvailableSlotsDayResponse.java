package dev.winclip.vetclinic.doctor.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record AvailableSlotsDayResponse(
		@Schema(description = "Calendar day in clinic timezone", example = "2026-04-15")
		LocalDate date,
		@Schema(description = "UTC start instants of free 30-minute slots (same rules as single-day endpoint)")
		List<Instant> slots) {
}
