package dev.winclip.vetclinic.doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	Page<Doctor> findAllByActiveTrue(Pageable pageable);

	boolean existsByEmail(String email);

	boolean existsByVeterinaryLicense(String veterinaryLicense);

	boolean existsByEmailAndIdNot(String email, Long id);

	boolean existsByVeterinaryLicenseAndIdNot(String veterinaryLicense, Long id);
}
