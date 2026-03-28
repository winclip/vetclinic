package dev.winclip.vetclinic.doctor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.doctor.dto.DoctorCreateRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

	private final DoctorRepository doctorRepository;

	@Transactional(readOnly = true)
	public List<DoctorResponse> findAllActive() {
		return doctorRepository.findAllByActiveTrueOrderByLastNameAscFirstNameAsc().stream()
				.map(DoctorResponse::from)
				.toList();
	}

	@Transactional
	public DoctorResponse create(DoctorCreateRequest request) {
		Doctor doctor = new Doctor();
		applyRequest(doctor, request);
		return DoctorResponse.from(doctorRepository.save(doctor));
	}

	@Transactional
	public DoctorResponse update(Long id, DoctorCreateRequest request) {
		Doctor doctor = doctorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
		applyRequest(doctor, request);
		return DoctorResponse.from(doctorRepository.save(doctor));
	}

	private static void applyRequest(Doctor doctor, DoctorCreateRequest request) {
		doctor.setFirstName(request.firstName());
		doctor.setLastName(request.lastName());
		doctor.setSpecialization(request.specialization());
		doctor.setPhone(request.phone());
		doctor.setEmail(request.email());
		doctor.setVeterinaryLicense(request.veterinaryLicense());
		doctor.setHiredOn(request.hiredOn());
		if (request.active() != null) {
			doctor.setActive(request.active());
		}
	}
}
