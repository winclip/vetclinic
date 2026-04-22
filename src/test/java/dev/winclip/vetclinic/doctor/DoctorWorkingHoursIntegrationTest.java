package dev.winclip.vetclinic.doctor;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.winclip.vetclinic.doctor.dto.DoctorCreateRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorResponse;
import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursItemRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursReplaceRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursUiInterval;
import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursWeekScheduleResponse;
import dev.winclip.vetclinic.doctor.dto.WeekdayCode;
import dev.winclip.vetclinic.error.ErrorResponse;
import dev.winclip.vetclinic.support.AbstractIntegrationTest;

class DoctorWorkingHoursIntegrationTest extends AbstractIntegrationTest {

	private static final LocalTime MORNING_FROM = LocalTime.of(9, 0);
	private static final LocalTime MORNING_TO = LocalTime.of(12, 0);
	private static final LocalTime AFTERNOON_FROM = LocalTime.of(13, 0);
	private static final LocalTime AFTERNOON_TO = LocalTime.of(17, 0);

	@Test
	void adminReplacesScheduleAndGetReturnsGroupedFormat() {
		String bearer = createAdmin("charon_admin", "continental-1");

		DoctorCreateRequest createDoctor = new DoctorCreateRequest(
				"John",
				"Wick",
				"Ophthalmology",
				"+15550000101",
				"john.wick+vet-" + System.nanoTime() + "@continental.example",
				"VET-LIC-JW-" + System.nanoTime(),
				null,
				null,
				null,
				null,
				null,
				Boolean.TRUE);

		ResponseEntity<DoctorResponse> created = exchange(
				HttpMethod.POST, "/api/doctors", bearer, createDoctor, DoctorResponse.class);

		assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(created.getBody()).isNotNull();
		Long doctorId = created.getBody().id();

		List<DoctorWorkingHoursItemRequest> intervals = List.of(
				new DoctorWorkingHoursItemRequest(2, MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursItemRequest(2, AFTERNOON_FROM, AFTERNOON_TO),
				new DoctorWorkingHoursItemRequest(3, MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursItemRequest(3, AFTERNOON_FROM, AFTERNOON_TO),
				new DoctorWorkingHoursItemRequest(4, MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursItemRequest(4, AFTERNOON_FROM, AFTERNOON_TO),
				new DoctorWorkingHoursItemRequest(5, MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursItemRequest(5, AFTERNOON_FROM, AFTERNOON_TO),
				new DoctorWorkingHoursItemRequest(6, MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursItemRequest(6, AFTERNOON_FROM, AFTERNOON_TO));

		ResponseEntity<Void> put = exchange(
				HttpMethod.PUT,
				"/api/doctors/" + doctorId + "/working-hours",
				bearer,
				new DoctorWorkingHoursReplaceRequest(intervals),
				Void.class);

		assertThat(put.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<DoctorWorkingHoursWeekScheduleResponse> got = http.getForEntity(
				"/api/doctors/" + doctorId + "/working-hours",
				DoctorWorkingHoursWeekScheduleResponse.class);

		assertThat(got.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(got.getBody()).isNotNull();

		var schedule = got.getBody().schedule();

		assertThat(schedule).containsOnlyKeys(
				WeekdayCode.MON, WeekdayCode.TUE, WeekdayCode.WED, WeekdayCode.THU,
				WeekdayCode.FRI, WeekdayCode.SAT, WeekdayCode.SUN);

		assertThat(schedule.get(WeekdayCode.MON)).isEmpty();
		assertThat(schedule.get(WeekdayCode.SUN)).isEmpty();

		List<DoctorWorkingHoursUiInterval> expectedWorkingDay = List.of(
				new DoctorWorkingHoursUiInterval(MORNING_FROM, MORNING_TO),
				new DoctorWorkingHoursUiInterval(AFTERNOON_FROM, AFTERNOON_TO));

		for (WeekdayCode day : List.of(
				WeekdayCode.TUE, WeekdayCode.WED, WeekdayCode.THU,
				WeekdayCode.FRI, WeekdayCode.SAT)) {
			assertThat(schedule.get(day))
					.as("schedule for %s", day)
					.containsExactlyElementsOf(expectedWorkingDay);
		}
	}

	@Test
	void putWorkingHoursWithoutTokenReturnsUnauthorized() {
		DoctorWorkingHoursReplaceRequest body = new DoctorWorkingHoursReplaceRequest(List.of(
				new DoctorWorkingHoursItemRequest(2, MORNING_FROM, MORNING_TO)));

		ResponseEntity<ErrorResponse> response = http.exchange(
				"/api/doctors/1/working-hours",
				HttpMethod.PUT,
				new HttpEntity<>(body),
				ErrorResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().code()).isEqualTo("UNAUTHORIZED");
		assertThat(response.getBody().message()).isNotBlank();
	}

	@Test
	void putWorkingHoursAsRegularUserReturnsForbidden() {
		String bearer = createUser("santino_dantonio", "marker-debt-1");

		DoctorWorkingHoursReplaceRequest body = new DoctorWorkingHoursReplaceRequest(List.of(
				new DoctorWorkingHoursItemRequest(2, MORNING_FROM, MORNING_TO)));

		ResponseEntity<ErrorResponse> response = exchange(
				HttpMethod.PUT,
				"/api/doctors/1/working-hours",
				bearer,
				body,
				ErrorResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().code()).isEqualTo("FORBIDDEN");
		assertThat(response.getBody().message()).isNotBlank();
	}
}
