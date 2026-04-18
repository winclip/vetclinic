package dev.winclip.vetclinic.doctor.dto;

import java.time.LocalTime;

public record DoctorWorkingHoursUiInterval(LocalTime from, LocalTime to) {

	public static DoctorWorkingHoursUiInterval from(DoctorWorkingHoursResponse row) {
		return new DoctorWorkingHoursUiInterval(row.startTime(), row.endTime());
	}
}
