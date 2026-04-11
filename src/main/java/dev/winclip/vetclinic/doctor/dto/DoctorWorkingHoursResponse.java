package dev.winclip.vetclinic.doctor.dto;

import java.time.LocalTime;

import dev.winclip.vetclinic.doctor.DoctorWorkingHours;

public record DoctorWorkingHoursResponse(
		Long id,
		int dayOfWeek,
		LocalTime startTime,
		LocalTime endTime) {

	public static DoctorWorkingHoursResponse from(DoctorWorkingHours row) {
		return new DoctorWorkingHoursResponse(
				row.getId(),
				row.getDayOfWeek(),
				row.getStartTime(),
				row.getEndTime());
	}
}
