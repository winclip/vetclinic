package dev.winclip.vetclinic.appointment;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.appointment.dto.AppointmentCreateRequest;
import dev.winclip.vetclinic.appointment.dto.AppointmentRescheduleRequest;
import dev.winclip.vetclinic.appointment.dto.AppointmentResponse;
import dev.winclip.vetclinic.doctor.Doctor;
import dev.winclip.vetclinic.doctor.DoctorRepository;
import dev.winclip.vetclinic.doctor.DoctorWorkingHours;
import dev.winclip.vetclinic.doctor.DoctorWorkingHoursRepository;
import dev.winclip.vetclinic.pet.Pet;
import dev.winclip.vetclinic.pet.PetRepository;
import dev.winclip.vetclinic.user.User;
import dev.winclip.vetclinic.user.UserRepository;
import dev.winclip.vetclinic.user.UserRole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

	private static final Duration APPOINTMENT_LENGTH = Duration.ofMinutes(30);

	private static final String USER_NOT_FOUND = "User not found";
	private static final String DOCTOR_NOT_FOUND = "Doctor not found";
	private static final String PET_NOT_FOUND = "Pet not found";
	private static final String APPOINTMENT_NOT_FOUND = "Appointment not found";

	private final AppointmentRepository appointmentRepository;
	private final DoctorRepository doctorRepository;
	private final DoctorWorkingHoursRepository workingHoursRepository;
	private final PetRepository petRepository;
	private final UserRepository userRepository;

	@Value("${vetclinic.clinic-timezone:UTC}")
	private String clinicTimezone;

	@Transactional(readOnly = true)
	public List<AppointmentResponse> listMine(String username) {
		User owner = requireUser(username);
		return appointmentRepository.findAllForOwnerOrderByStartsAtDesc(owner.getId()).stream()
				.map(AppointmentResponse::from)
				.toList();
	}

	@Transactional
	public AppointmentResponse create(String username, AppointmentCreateRequest request) {
		User owner = requireUser(username);
		Doctor doctor = requireActiveDoctor(request.doctorId());
		Pet pet = requireMyActivePet(owner.getId(), request.petId());

		Instant startsAt = request.startsAt();
		ZoneId zone = ZoneId.of(clinicTimezone);
		assertStartsOnHalfHourGrid(startsAt, zone);
		if (startsAt.isBefore(Instant.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment start time must be in the future");
		}

		Instant endsAt = startsAt.plus(APPOINTMENT_LENGTH);
		assertFitsSingleCalendarDay(startsAt, endsAt, zone);
		assertWithinWorkingHours(doctor.getId(), startsAt, endsAt, zone);

		if (appointmentRepository.existsOverlapForDoctor(doctor.getId(), AppointmentStatus.SCHEDULED, startsAt, endsAt, null)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This time slot overlaps another appointment");
		}

		Appointment entity = new Appointment();
		entity.setDoctor(doctor);
		entity.setPet(pet);
		entity.setStartsAt(startsAt);
		entity.setEndsAt(endsAt);
		entity.setStatus(AppointmentStatus.SCHEDULED);

		Appointment saved = appointmentRepository.save(entity);
		return AppointmentResponse.from(saved);
	}

	@Transactional
	public AppointmentResponse cancel(String username, Long appointmentId) {
		User actor = requireUser(username);
		Appointment a = appointmentRepository.findWithPetOwnerAndDoctorById(appointmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APPOINTMENT_NOT_FOUND));
		assertCanModifyAppointment(actor, a);
		if (a.getStatus() != AppointmentStatus.SCHEDULED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only scheduled appointments can be cancelled");
		}
		a.setStatus(AppointmentStatus.CANCELLED);
		return AppointmentResponse.from(appointmentRepository.save(a));
	}

	@Transactional
	public AppointmentResponse reschedule(String username, Long appointmentId, AppointmentRescheduleRequest request) {
		User actor = requireUser(username);
		Appointment a = appointmentRepository.findWithPetOwnerAndDoctorById(appointmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APPOINTMENT_NOT_FOUND));
		assertCanModifyAppointment(actor, a);
		if (a.getStatus() != AppointmentStatus.SCHEDULED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only scheduled appointments can be rescheduled");
		}
		Doctor doctor = a.getDoctor();
		if (!doctor.isActive()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor is no longer active");
		}

		Instant startsAt = request.startsAt();
		ZoneId zone = ZoneId.of(clinicTimezone);
		assertStartsOnHalfHourGrid(startsAt, zone);
		if (startsAt.isBefore(Instant.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment start time must be in the future");
		}

		Instant endsAt = startsAt.plus(APPOINTMENT_LENGTH);
		assertFitsSingleCalendarDay(startsAt, endsAt, zone);
		assertWithinWorkingHours(doctor.getId(), startsAt, endsAt, zone);

		if (appointmentRepository.existsOverlapForDoctor(doctor.getId(), AppointmentStatus.SCHEDULED, startsAt, endsAt,
				a.getId())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This time slot overlaps another appointment");
		}

		a.setStartsAt(startsAt);
		a.setEndsAt(endsAt);
		return AppointmentResponse.from(appointmentRepository.save(a));
	}

	private static void assertCanModifyAppointment(User actor, Appointment appointment) {
		if (actor.getRole() == UserRole.ADMIN) {
			return;
		}
		if (appointment.getPet().getOwner().getId().equals(actor.getId())) {
			return;
		}
		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to modify this appointment");
	}

	private User requireUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
	}

	private Doctor requireActiveDoctor(Long doctorId) {
		if (doctorId == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND);
		}
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND));
		if (!doctor.isActive()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND);
		}
		return doctor;
	}

	private Pet requireMyActivePet(Long ownerId, Long petId) {
		if (petId == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, PET_NOT_FOUND);
		}
		return petRepository.findByIdAndOwnerIdAndActiveTrue(petId, ownerId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PET_NOT_FOUND));
	}

	private static void assertStartsOnHalfHourGrid(Instant startsAt, ZoneId zone) {
		ZonedDateTime z = startsAt.atZone(zone);
		if (z.getSecond() != 0 || z.getNano() != 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Appointment start must be on a full or half hour (seconds must be zero)");
		}
		int minute = z.getMinute();
		if (minute != 0 && minute != 30) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Appointment start must be at :00 or :30 in the clinic timezone");
		}
	}

	private static void assertFitsSingleCalendarDay(Instant startsAt, Instant endsAt, ZoneId zone) {
		ZonedDateTime zStart = startsAt.atZone(zone);
		ZonedDateTime zEnd = endsAt.atZone(zone);
		if (!zStart.toLocalDate().equals(zEnd.toLocalDate())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Appointment must fit within one calendar day in the clinic timezone");
		}
	}

	private void assertWithinWorkingHours(Long doctorId, Instant startsAt, Instant endsAt, ZoneId zone) {
		ZonedDateTime zStart = startsAt.atZone(zone);
		int dayOfWeek = zStart.getDayOfWeek().getValue();
		LocalTime tStart = zStart.toLocalTime();
		LocalTime tEnd = endsAt.atZone(zone).toLocalTime();

		boolean anyRow = false;
		for (DoctorWorkingHours row : workingHoursRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctorId)) {
			if (row.getDayOfWeek() != dayOfWeek) {
				continue;
			}
			anyRow = true;
			if (!tStart.isBefore(row.getStartTime()) && !tEnd.isAfter(row.getEndTime())) {
				return;
			}
		}
		if (!anyRow) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor has no working hours for this day");
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Outside doctor working hours");
	}
}
