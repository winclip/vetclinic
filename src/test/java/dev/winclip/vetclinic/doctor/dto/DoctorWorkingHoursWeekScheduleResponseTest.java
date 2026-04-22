package dev.winclip.vetclinic.doctor.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class DoctorWorkingHoursWeekScheduleResponseTest {

	private static final LocalTime MORNING_FROM = LocalTime.of(9, 0);
	private static final LocalTime MORNING_TO = LocalTime.of(12, 0);
	private static final LocalTime AFTERNOON_FROM = LocalTime.of(13, 0);
	private static final LocalTime AFTERNOON_TO = LocalTime.of(17, 0);

	@Test
	void fromFlatWithEmptyInputReturnsAllDaysEmpty() {
		DoctorWorkingHoursWeekScheduleResponse response =
				DoctorWorkingHoursWeekScheduleResponse.fromFlat(List.of());

		assertThat(response.schedule()).containsOnlyKeys(WeekdayCode.values());
		for (WeekdayCode day : WeekdayCode.values()) {
			assertThat(response.schedule().get(day))
					.as("schedule for %s", day)
					.isEmpty();
		}
	}

	@Test
	void fromFlatGroupsIntervalsByWeekday() {
		List<DoctorWorkingHoursResponse> rows = List.of(
				new DoctorWorkingHoursResponse(1L, 1, MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursResponse(2L, 3, MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursResponse(3L, 5, AFTERNOON_FROM, AFTERNOON_TO));

		var schedule = DoctorWorkingHoursWeekScheduleResponse.fromFlat(rows).schedule();

		assertThat(schedule.get(WeekdayCode.MON))
				.containsExactly(new DoctorWorkingHoursUiInterval(MORNING_FROM, MORNING_TO));
		assertThat(schedule.get(WeekdayCode.WED))
				.containsExactly(new DoctorWorkingHoursUiInterval(MORNING_FROM, MORNING_TO));
		assertThat(schedule.get(WeekdayCode.FRI))
				.containsExactly(new DoctorWorkingHoursUiInterval(AFTERNOON_FROM, AFTERNOON_TO));

		for (WeekdayCode day : List.of(WeekdayCode.TUE, WeekdayCode.THU, WeekdayCode.SAT, WeekdayCode.SUN)) {
			assertThat(schedule.get(day))
					.as("schedule for %s", day)
					.isEmpty();
		}
	}

	@Test
	void fromFlatSortsIntervalsWithinDayByStartTime() {
		List<DoctorWorkingHoursResponse> rows = List.of(
				new DoctorWorkingHoursResponse(1L, 2, AFTERNOON_FROM, AFTERNOON_TO),
				new DoctorWorkingHoursResponse(2L, 2, MORNING_FROM, MORNING_TO));

		var schedule = DoctorWorkingHoursWeekScheduleResponse.fromFlat(rows).schedule();

		assertThat(schedule.get(WeekdayCode.TUE)).containsExactly(
				new DoctorWorkingHoursUiInterval(MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursUiInterval(AFTERNOON_FROM, AFTERNOON_TO));
	}

	@Test
	void fromFlatPropagatesIllegalArgumentExceptionForOutOfRangeDayOfWeek() {
		List<DoctorWorkingHoursResponse> rows = List.of(
				new DoctorWorkingHoursResponse(1L, 8, MORNING_FROM, MORNING_TO));

		assertThatThrownBy(() -> DoctorWorkingHoursWeekScheduleResponse.fromFlat(rows))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("dayOfWeek");
	}
}
