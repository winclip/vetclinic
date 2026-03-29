package dev.winclip.vetclinic.doctor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	List<Doctor> findAllByActiveTrueOrderByLastNameAscFirstNameAsc();

	boolean existsByEmail(String email);

	boolean existsByVeterinaryLicense(String veterinaryLicense);

	boolean existsByEmailAndIdNot(String email, Long id);

	boolean existsByVeterinaryLicenseAndIdNot(String veterinaryLicense, Long id);
}
