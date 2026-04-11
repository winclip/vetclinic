package dev.winclip.vetclinic.doctor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.appointment.Appointment;
import dev.winclip.vetclinic.appointment.AppointmentRepository;
import dev.winclip.vetclinic.appointment.AppointmentStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityService {

	private static final Duration SLOT = Duration.ofMinutes(30);

	private static final String DOCTOR_NOT_FOUND = "Doctor not found";

	private final DoctorRepository doctorRepository;
	private final DoctorWorkingHoursRepository workingHoursRepository;
	private final AppointmentRepository appointmentRepository;

	@Value("${vetclinic.clinic-timezone:UTC}")
	private String clinicTimezone;

	@Transactional(readOnly = true)
	public List<Instant> getAvailableSlots(Long doctorId, LocalDate date) {
		if (date == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date is required");
		}
		ZoneId zone = ZoneId.of(clinicTimezone);
		LocalDate today = LocalDate.now(zone);
		if (date.isBefore(today)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date must be today or in the future");
		}

		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND));
		if (!doctor.isActive()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND);
		}

		int dayOfWeek = date.getDayOfWeek().getValue();
		NavigableSet<LocalTime> slotStarts = new TreeSet<>();
		for (DoctorWorkingHours row : workingHoursRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctorId)) {
			if (row.getDayOfWeek() != dayOfWeek) {
				continue;
			}
			LocalTime end = row.getEndTime();
			for (LocalTime t = row.getStartTime(); !t.plusMinutes(30).isAfter(end); t = t.plusMinutes(30)) {
				slotStarts.add(t);
			}
		}

		Instant rangeStart = date.atStartOfDay(zone).toInstant();
		Instant rangeEnd = date.plusDays(1).atStartOfDay(zone).toInstant();
		List<Appointment> busy = appointmentRepository.findForDoctorStatusOverlappingRange(
				doctorId, AppointmentStatus.SCHEDULED, rangeStart, rangeEnd);

		Instant now = Instant.now();
		List<Instant> result = new ArrayList<>();
		for (LocalTime t : slotStarts) {
			Instant slotStart = date.atTime(t).atZone(zone).toInstant();
			Instant slotEnd = slotStart.plus(SLOT);
			if (date.equals(today) && slotStart.isBefore(now)) {
				continue;
			}
			if (overlapsBusy(slotStart, slotEnd, busy)) {
				continue;
			}
			result.add(slotStart);
		}
		return result;
	}

	private static boolean overlapsBusy(Instant slotStart, Instant slotEnd, List<Appointment> busy) {
		for (Appointment a : busy) {
			if (slotStart.isBefore(a.getEndsAt()) && slotEnd.isAfter(a.getStartsAt())) {
				return true;
			}
		}
		return false;
	}
}
