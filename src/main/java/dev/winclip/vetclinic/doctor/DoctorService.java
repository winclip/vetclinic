package dev.winclip.vetclinic.doctor;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Objects;

import dev.winclip.vetclinic.doctor.dto.DoctorCreateRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

	private static final String DOCTOR_NOT_FOUND = "Doctor not found";

	private final DoctorRepository doctorRepository;

	@Transactional(readOnly = true)
	public Page<DoctorResponse> findAllActive(Pageable pageable) {
		return doctorRepository.findAllByActiveTrue(pageable)
				.map(DoctorResponse::from);
	}

	@Transactional(readOnly = true)
	public DoctorResponse getById(Long id) {
		Doctor doctor = requireDoctor(id);
		if (!doctor.isActive()) {
			throw doctorNotFound();
		}
		return DoctorResponse.from(doctor);
	}

	@Transactional
	public DoctorResponse create(DoctorCreateRequest request) {
		assertNoDuplicateEmailOrLicense(request.email(), request.veterinaryLicense(), null);
		Doctor doctor = new Doctor();
		applyRequest(doctor, request);
		return DoctorResponse.from(doctorRepository.save(doctor));
	}

	@Transactional
	public DoctorResponse update(Long id, DoctorCreateRequest request) {
		if (id == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor id is required");
		}
		Doctor doctor = requireDoctor(id);
		assertNoDuplicateEmailOrLicense(request.email(), request.veterinaryLicense(), id);
		applyRequest(doctor, request);
		Doctor saved = doctorRepository.save(Objects.requireNonNull(doctor));
		return DoctorResponse.from(Objects.requireNonNull(saved));
	}

	@Transactional
	public void softDelete(Long id) {
		if (id == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor id is required");
		}
		Doctor doctor = requireDoctor(id);
		if (doctor.isActive()) {
			doctor.setActive(false);
			doctorRepository.save(doctor);
		}
	}

	private Doctor requireDoctor(Long id) {
		if (id == null) {
			throw doctorNotFound();
		}
		return doctorRepository.findById(id).orElseThrow(this::doctorNotFound);
	}

	private ResponseStatusException doctorNotFound() {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, DOCTOR_NOT_FOUND);
	}

	private void assertNoDuplicateEmailOrLicense(String email, String veterinaryLicense, Long excludeDoctorId) {
		if (email != null && !email.isBlank()) {
			boolean taken = excludeDoctorId == null
					? doctorRepository.existsByEmail(email)
					: doctorRepository.existsByEmailAndIdNot(email, excludeDoctorId);
			if (taken) {
				throw new DuplicateDoctorException("DUPLICATE_EMAIL", "A doctor with this email already exists");
			}
		}
		if (veterinaryLicense != null && !veterinaryLicense.isBlank()) {
			boolean taken = excludeDoctorId == null
					? doctorRepository.existsByVeterinaryLicense(veterinaryLicense)
					: doctorRepository.existsByVeterinaryLicenseAndIdNot(veterinaryLicense, excludeDoctorId);
			if (taken) {
				throw new DuplicateDoctorException("DUPLICATE_VETERINARY_LICENSE",
						"This veterinary license is already registered");
			}
		}
	}

	private static void applyRequest(Doctor doctor, DoctorCreateRequest request) {
		doctor.setFirstName(request.firstName());
		doctor.setLastName(request.lastName());
		doctor.setSpecialization(request.specialization());
		doctor.setPhone(request.phone());
		doctor.setEmail(request.email());
		doctor.setVeterinaryLicense(request.veterinaryLicense());
		doctor.setBio(request.bio());
		doctor.setPhotoUrl(request.photoUrl());
		doctor.setDateOfBirth(request.dateOfBirth());
		doctor.setYearsOfExperience(request.yearsOfExperience());
		doctor.setHiredOn(request.hiredOn());
		if (request.active() != null) {
			doctor.setActive(request.active());
		}
	}
}

