package dev.winclip.vetclinic.doctor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursItemRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorWorkingHoursService {

	private static final String DOCTOR_NOT_FOUND = "Doctor not found";

	private final DoctorRepository doctorRepository;
	private final DoctorWorkingHoursRepository workingHoursRepository;

	@Transactional(readOnly = true)
	public List<DoctorWorkingHoursResponse> getWorkingHours(Long doctorId) {
		requireDoctor(doctorId);
		return workingHoursRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctorId).stream()
				.map(DoctorWorkingHoursResponse::from)
				.toList();
	}

	@Transactional
	public void replaceWorkingHours(Long doctorId, List<DoctorWorkingHoursItemRequest> items) {
		Doctor doctor = requireDoctor(doctorId);
		List<DoctorWorkingHoursItemRequest> normalized = items == null ? List.of() : items;
		validateSlots(normalized);

		workingHoursRepository.deleteByDoctorId(doctorId);

		if (normalized.isEmpty()) {
			return;
		}

		List<DoctorWorkingHours> rows = new ArrayList<>();
		for (DoctorWorkingHoursItemRequest item : normalized) {
			DoctorWorkingHours row = new DoctorWorkingHours();
			row.setDoctor(doctor);
			row.setDayOfWeek(item.dayOfWeek());
			row.setStartTime(item.startTime());
			row.setEndTime(item.endTime());
			rows.add(row);
		}
		workingHoursRepository.saveAll(rows);
	}

	private Doctor requireDoctor(Long id) {
		if (id == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND);
		}
		return doctorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND));
	}

	private static void validateSlots(List<DoctorWorkingHoursItemRequest> items) {
		for (DoctorWorkingHoursItemRequest item : items) {
			if (item.endTime().compareTo(item.startTime()) <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each interval must have endTime after startTime");
			}
		}

		List<DoctorWorkingHoursItemRequest> sorted = new ArrayList<>(items);
		sorted.sort(Comparator.comparingInt(DoctorWorkingHoursItemRequest::dayOfWeek)
				.thenComparing(DoctorWorkingHoursItemRequest::startTime));

		for (int i = 0; i < sorted.size() - 1; i++) {
			DoctorWorkingHoursItemRequest a = sorted.get(i);
			DoctorWorkingHoursItemRequest b = sorted.get(i + 1);
			if (a.dayOfWeek() != b.dayOfWeek()) {
				continue;
			}
			if (a.endTime().isAfter(b.startTime())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Working hours intervals overlap on the same day");
			}
		}
	}
}
