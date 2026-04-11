package dev.winclip.vetclinic.doctor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorWorkingHoursRepository extends JpaRepository<DoctorWorkingHours, Long> {

	List<DoctorWorkingHours> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(Long doctorId);

	void deleteByDoctorId(Long doctorId);
}
