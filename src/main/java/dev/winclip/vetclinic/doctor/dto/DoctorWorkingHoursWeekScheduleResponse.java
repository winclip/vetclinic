package dev.winclip.vetclinic.doctor.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record DoctorWorkingHoursWeekScheduleResponse(Map<WeekdayCode, List<DoctorWorkingHoursUiInterval>> schedule) {

	public static DoctorWorkingHoursWeekScheduleResponse fromFlat(List<DoctorWorkingHoursResponse> rows) {
		Map<WeekdayCode, List<DoctorWorkingHoursUiInterval>> map = new EnumMap<>(WeekdayCode.class);
		for (WeekdayCode d : WeekdayCode.values()) {
			map.put(d, new ArrayList<>());
		}
		for (DoctorWorkingHoursResponse row : rows) {
			WeekdayCode day = WeekdayCode.fromIsoDayOfWeek(row.dayOfWeek());
			map.get(day).add(DoctorWorkingHoursUiInterval.from(row));
		}
		Map<WeekdayCode, List<DoctorWorkingHoursUiInterval>> immutable = new EnumMap<>(WeekdayCode.class);
		for (WeekdayCode d : WeekdayCode.values()) {
			List<DoctorWorkingHoursUiInterval> sorted = map.get(d).stream()
					.sorted(Comparator.comparing(DoctorWorkingHoursUiInterval::from))
					.toList();
			immutable.put(d, sorted);
		}
		return new DoctorWorkingHoursWeekScheduleResponse(Map.copyOf(immutable));
	}
}
