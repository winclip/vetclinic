package dev.winclip.vetclinic.doctor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		return DoctorResponse.from(doctorRepository.save(doctor));
	}
}
