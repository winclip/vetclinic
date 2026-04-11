package dev.winclip.vetclinic.doctor.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DoctorWorkingHoursReplaceRequest(
		@NotNull @Valid List<@Valid DoctorWorkingHoursItemRequest> intervals) {
}
